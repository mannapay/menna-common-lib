package com.mannapay.common.events.kafka.producer;

import com.mannapay.common.events.core.DomainEvent;
import com.mannapay.common.events.core.EventEnvelope;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Enterprise-grade event publisher for the MannaPay platform.
 *
 * Features:
 * - Async publishing with CompletableFuture
 * - Event envelope wrapping for transport metadata
 * - Correlation ID propagation
 * - Metrics collection
 * - Structured logging
 * - Error handling with callbacks
 */
@Component
@Slf4j
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MeterRegistry meterRegistry;

    private final Counter eventsPublishedCounter;
    private final Counter eventsFailedCounter;
    private final Timer publishTimer;

    public EventPublisher(KafkaTemplate<String, Object> kafkaTemplate, MeterRegistry meterRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.meterRegistry = meterRegistry;

        // Initialize metrics
        this.eventsPublishedCounter = Counter.builder("mannapay.events.published")
            .description("Total number of events published")
            .register(meterRegistry);

        this.eventsFailedCounter = Counter.builder("mannapay.events.failed")
            .description("Total number of failed event publications")
            .register(meterRegistry);

        this.publishTimer = Timer.builder("mannapay.events.publish.time")
            .description("Time taken to publish events")
            .register(meterRegistry);
    }

    /**
     * Publish a domain event to its default topic.
     *
     * @param event The domain event to publish
     * @return CompletableFuture with the send result
     */
    public CompletableFuture<SendResult<String, Object>> publish(DomainEvent event) {
        return publish(event, event.getTopicName());
    }

    /**
     * Publish a domain event to a specific topic.
     *
     * @param event The domain event to publish
     * @param topic The target topic
     * @return CompletableFuture with the send result
     */
    public CompletableFuture<SendResult<String, Object>> publish(DomainEvent event, String topic) {
        // Ensure event has required fields
        event.initializeDefaults();

        // Wrap in envelope
        EventEnvelope<DomainEvent> envelope = EventEnvelope.wrap(event, topic);

        return publishEnvelope(envelope);
    }

    /**
     * Publish an event envelope.
     *
     * @param envelope The event envelope to publish
     * @return CompletableFuture with the send result
     */
    public CompletableFuture<SendResult<String, Object>> publishEnvelope(EventEnvelope<? extends DomainEvent> envelope) {
        long startTime = System.nanoTime();

        DomainEvent event = envelope.getPayload();
        String topic = envelope.getTopic();
        String key = envelope.getPartitionKey();

        // Create producer record with headers
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, key, envelope);

        // Add headers for tracing and metadata
        addHeaders(record, envelope);

        log.debug("Publishing event: topic={}, key={}, eventType={}, eventId={}, correlationId={}",
            topic, key, event.getEventType(), event.getEventId(), event.getCorrelationId());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(record);

        future.whenComplete((result, ex) -> {
            long duration = System.nanoTime() - startTime;
            publishTimer.record(duration, TimeUnit.NANOSECONDS);

            if (ex != null) {
                eventsFailedCounter.increment();
                log.error("Failed to publish event: topic={}, eventId={}, error={}",
                    topic, event.getEventId(), ex.getMessage(), ex);
            } else {
                eventsPublishedCounter.increment();
                envelope.markPublished();

                log.info("Event published successfully: topic={}, partition={}, offset={}, eventType={}, eventId={}",
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset(),
                    event.getEventType(),
                    event.getEventId());
            }
        });

        return future;
    }

    /**
     * Publish an event with correlation to a parent event.
     *
     * @param event The event to publish
     * @param parentEvent The parent event to correlate with
     * @return CompletableFuture with the send result
     */
    public CompletableFuture<SendResult<String, Object>> publishCorrelated(
            DomainEvent event, DomainEvent parentEvent) {

        event.inheritCorrelation(parentEvent);
        return publish(event);
    }

    /**
     * Publish an event with a specific correlation ID.
     *
     * @param event The event to publish
     * @param correlationId The correlation ID
     * @return CompletableFuture with the send result
     */
    public CompletableFuture<SendResult<String, Object>> publishWithCorrelation(
            DomainEvent event, String correlationId) {

        event.setCorrelationId(correlationId);
        return publish(event);
    }

    /**
     * Publish an event synchronously (blocking).
     *
     * @param event The event to publish
     * @param timeoutMs Timeout in milliseconds
     * @return The send result
     * @throws Exception if publishing fails or times out
     */
    public SendResult<String, Object> publishSync(DomainEvent event, long timeoutMs) throws Exception {
        return publish(event).get(timeoutMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Add headers to the producer record.
     */
    private void addHeaders(ProducerRecord<String, Object> record, EventEnvelope<?> envelope) {
        DomainEvent event = envelope.getPayload();

        // Event metadata headers
        addHeader(record, "eventId", event.getEventId());
        addHeader(record, "eventType", event.getEventType());
        addHeader(record, "aggregateType", event.getAggregateType());
        addHeader(record, "aggregateId", event.getAggregateId());
        addHeader(record, "timestamp", event.getTimestamp().toString());
        addHeader(record, "schemaVersion", String.valueOf(event.getSchemaVersion()));

        // Correlation headers
        if (event.getCorrelationId() != null) {
            addHeader(record, "correlationId", event.getCorrelationId());
        }
        if (event.getCausationId() != null) {
            addHeader(record, "causationId", event.getCausationId());
        }

        // Tracing headers
        if (envelope.getTraceId() != null) {
            addHeader(record, "traceId", envelope.getTraceId());
        }
        if (envelope.getSpanId() != null) {
            addHeader(record, "spanId", envelope.getSpanId());
        }

        // Idempotency
        addHeader(record, "idempotencyKey", envelope.getIdempotencyKey());

        // Content type
        addHeader(record, "content-type", "application/json");

        // Source service
        if (event.getSource() != null) {
            addHeader(record, "source", event.getSource());
        }
    }

    private void addHeader(ProducerRecord<String, Object> record, String key, String value) {
        if (value != null) {
            record.headers().add(new RecordHeader(key, value.getBytes(StandardCharsets.UTF_8)));
        }
    }

    /**
     * Create a new correlation ID.
     */
    public String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
