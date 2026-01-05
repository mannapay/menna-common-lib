package com.mannapay.common.events.kafka.consumer;

import com.mannapay.common.events.core.DomainEvent;
import com.mannapay.common.events.core.EventEnvelope;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Idempotent event processor ensuring exactly-once processing semantics.
 *
 * Features:
 * - Redis-based idempotency tracking
 * - Metrics collection
 * - Structured logging with correlation IDs
 * - Graceful error handling
 */
@Component
@Slf4j
public class IdempotentEventProcessor {

    private static final String PROCESSED_KEY_PREFIX = "mannapay:events:processed:";
    private static final Duration IDEMPOTENCY_TTL = Duration.ofDays(7);

    private final StringRedisTemplate redisTemplate;
    private final MeterRegistry meterRegistry;

    private final Counter eventsProcessedCounter;
    private final Counter eventsDuplicateCounter;
    private final Counter eventsFailedCounter;
    private final Timer processingTimer;

    public IdempotentEventProcessor(StringRedisTemplate redisTemplate, MeterRegistry meterRegistry) {
        this.redisTemplate = redisTemplate;
        this.meterRegistry = meterRegistry;

        this.eventsProcessedCounter = Counter.builder("mannapay.events.processed")
            .description("Total number of events processed")
            .register(meterRegistry);

        this.eventsDuplicateCounter = Counter.builder("mannapay.events.duplicate")
            .description("Total number of duplicate events skipped")
            .register(meterRegistry);

        this.eventsFailedCounter = Counter.builder("mannapay.events.processing.failed")
            .description("Total number of failed event processing attempts")
            .register(meterRegistry);

        this.processingTimer = Timer.builder("mannapay.events.processing.time")
            .description("Time taken to process events")
            .register(meterRegistry);
    }

    /**
     * Process an event with idempotency guarantee.
     *
     * @param record The Kafka consumer record
     * @param handler The event handler
     * @param ack The acknowledgment
     * @param <T> The event type
     */
    public <T extends DomainEvent> void processIdempotently(
            ConsumerRecord<String, EventEnvelope<T>> record,
            EventHandler<T> handler,
            Acknowledgment ack) {

        EventEnvelope<T> envelope = record.value();
        T event = envelope.getPayload();
        String idempotencyKey = envelope.getIdempotencyKey();

        long startTime = System.nanoTime();

        log.debug("Processing event: topic={}, partition={}, offset={}, eventType={}, eventId={}, correlationId={}",
            record.topic(),
            record.partition(),
            record.offset(),
            event.getEventType(),
            event.getEventId(),
            event.getCorrelationId());

        try {
            // Check idempotency
            if (isAlreadyProcessed(idempotencyKey)) {
                log.info("Duplicate event detected, skipping: eventId={}, idempotencyKey={}",
                    event.getEventId(), idempotencyKey);
                eventsDuplicateCounter.increment();
                ack.acknowledge();
                return;
            }

            // Process the event
            handler.handle(event);

            // Mark as processed
            markAsProcessed(idempotencyKey, event);

            // Acknowledge
            ack.acknowledge();

            eventsProcessedCounter.increment();
            long duration = System.nanoTime() - startTime;
            processingTimer.record(duration, TimeUnit.NANOSECONDS);

            log.info("Event processed successfully: eventType={}, eventId={}, correlationId={}, durationMs={}",
                event.getEventType(),
                event.getEventId(),
                event.getCorrelationId(),
                TimeUnit.NANOSECONDS.toMillis(duration));

        } catch (EventHandlerException e) {
            handleProcessingError(record, envelope, e, ack);
        } catch (Exception e) {
            handleProcessingError(record, envelope,
                new EventHandlerException("Unexpected error", e, true), ack);
        }
    }

    /**
     * Process a raw domain event (without envelope).
     */
    public <T extends DomainEvent> void processRawEventIdempotently(
            ConsumerRecord<String, T> record,
            EventHandler<T> handler,
            Acknowledgment ack) {

        T event = record.value();
        String idempotencyKey = getIdempotencyKeyFromHeaders(record);
        if (idempotencyKey == null) {
            idempotencyKey = event.getEventId();
        }

        long startTime = System.nanoTime();

        try {
            if (isAlreadyProcessed(idempotencyKey)) {
                log.info("Duplicate event detected, skipping: eventId={}", event.getEventId());
                eventsDuplicateCounter.increment();
                ack.acknowledge();
                return;
            }

            handler.handle(event);
            markAsProcessed(idempotencyKey, event);
            ack.acknowledge();

            eventsProcessedCounter.increment();
            long duration = System.nanoTime() - startTime;
            processingTimer.record(duration, TimeUnit.NANOSECONDS);

            log.info("Event processed: eventType={}, eventId={}, durationMs={}",
                event.getEventType(), event.getEventId(), TimeUnit.NANOSECONDS.toMillis(duration));

        } catch (Exception e) {
            eventsFailedCounter.increment();
            log.error("Event processing failed: eventType={}, eventId={}, error={}",
                event.getEventType(), event.getEventId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Check if an event has already been processed.
     */
    public boolean isAlreadyProcessed(String idempotencyKey) {
        String key = PROCESSED_KEY_PREFIX + idempotencyKey;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Mark an event as processed.
     */
    public void markAsProcessed(String idempotencyKey, DomainEvent event) {
        String key = PROCESSED_KEY_PREFIX + idempotencyKey;
        String value = String.format("%s|%s|%s",
            event.getEventType(),
            event.getEventId(),
            Instant.now().toString());
        redisTemplate.opsForValue().set(key, value, IDEMPOTENCY_TTL);
    }

    /**
     * Handle processing errors.
     */
    private void handleProcessingError(
            ConsumerRecord<String, ?> record,
            EventEnvelope<? extends DomainEvent> envelope,
            EventHandlerException e,
            Acknowledgment ack) {

        DomainEvent event = envelope.getPayload();
        eventsFailedCounter.increment();

        log.error("Event processing failed: topic={}, eventType={}, eventId={}, correlationId={}, retryable={}, error={}",
            record.topic(),
            event.getEventType(),
            event.getEventId(),
            event.getCorrelationId(),
            e.isRetryable(),
            e.getMessage(),
            e);

        if (!e.isRetryable()) {
            // Non-retryable errors - acknowledge to prevent infinite loop
            // The error handler will send to DLQ
            log.warn("Non-retryable error, acknowledging to send to DLQ: eventId={}", event.getEventId());
        }

        // Re-throw to trigger retry/DLQ handling
        throw e;
    }

    /**
     * Extract idempotency key from Kafka headers.
     */
    private String getIdempotencyKeyFromHeaders(ConsumerRecord<String, ?> record) {
        Header header = record.headers().lastHeader("idempotencyKey");
        if (header != null) {
            return new String(header.value(), StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * Extract correlation ID from Kafka headers.
     */
    public String getCorrelationIdFromHeaders(ConsumerRecord<String, ?> record) {
        Header header = record.headers().lastHeader("correlationId");
        if (header != null) {
            return new String(header.value(), StandardCharsets.UTF_8);
        }
        return null;
    }
}
