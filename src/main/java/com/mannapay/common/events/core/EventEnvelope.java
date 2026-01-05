package com.mannapay.common.events.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Event envelope that wraps domain events with transport metadata.
 *
 * The envelope pattern separates business event data from transport concerns:
 * - Routing information (topic, partition key)
 * - Delivery guarantees (idempotency key, retry count)
 * - Tracing headers (trace ID, span ID)
 * - Dead letter queue information
 *
 * This ensures events can be properly processed, tracked, and replayed
 * across the distributed system.
 *
 * @param <T> The type of domain event being wrapped
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventEnvelope<T extends DomainEvent> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique envelope ID for tracking this specific delivery.
     */
    private String envelopeId;

    /**
     * The wrapped domain event payload.
     */
    private T payload;

    /**
     * Idempotency key for exactly-once processing.
     * Consumers should track this to prevent duplicate processing.
     */
    private String idempotencyKey;

    /**
     * Target Kafka topic.
     */
    private String topic;

    /**
     * Partition key for ordering.
     */
    private String partitionKey;

    /**
     * Time when the envelope was created.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createdAt;

    /**
     * Time when the event was published to Kafka.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant publishedAt;

    /**
     * Number of times this event has been retried.
     */
    private int retryCount;

    /**
     * Maximum number of retries before sending to DLQ.
     */
    private int maxRetries;

    /**
     * Distributed tracing trace ID.
     */
    private String traceId;

    /**
     * Distributed tracing span ID.
     */
    private String spanId;

    /**
     * Parent span ID for trace hierarchy.
     */
    private String parentSpanId;

    /**
     * Original topic (for DLQ events).
     */
    private String originalTopic;

    /**
     * Reason for DLQ (if applicable).
     */
    private String dlqReason;

    /**
     * Last error message (for retries/DLQ).
     */
    private String lastError;

    /**
     * Priority level (0 = normal, higher = higher priority).
     */
    private int priority;

    /**
     * Content type (default: application/json).
     */
    private String contentType;

    /**
     * Headers for additional transport metadata.
     */
    private Map<String, String> headers;

    /**
     * Create an envelope for a domain event with default settings.
     */
    public static <E extends DomainEvent> EventEnvelope<E> wrap(E event) {
        return EventEnvelope.<E>builder()
            .envelopeId(UUID.randomUUID().toString())
            .payload(event)
            .idempotencyKey(event.getEventId())
            .topic(event.getTopicName())
            .partitionKey(event.getPartitionKey())
            .createdAt(Instant.now())
            .retryCount(0)
            .maxRetries(3)
            .priority(0)
            .contentType("application/json")
            .headers(new HashMap<>())
            .build();
    }

    /**
     * Create an envelope with custom topic.
     */
    public static <E extends DomainEvent> EventEnvelope<E> wrap(E event, String topic) {
        EventEnvelope<E> envelope = wrap(event);
        envelope.setTopic(topic);
        return envelope;
    }

    /**
     * Create an envelope with tracing information.
     */
    public static <E extends DomainEvent> EventEnvelope<E> wrapWithTracing(
            E event, String traceId, String spanId) {
        EventEnvelope<E> envelope = wrap(event);
        envelope.setTraceId(traceId);
        envelope.setSpanId(spanId);
        return envelope;
    }

    /**
     * Mark this envelope as published.
     */
    public void markPublished() {
        this.publishedAt = Instant.now();
    }

    /**
     * Increment retry count and record error.
     */
    public void recordRetry(String errorMessage) {
        this.retryCount++;
        this.lastError = errorMessage;
    }

    /**
     * Check if max retries exceeded.
     */
    public boolean shouldMoveToDlq() {
        return retryCount >= maxRetries;
    }

    /**
     * Mark as DLQ event.
     */
    public void markAsDlq(String reason) {
        this.originalTopic = this.topic;
        this.topic = getDlqTopic();
        this.dlqReason = reason;
    }

    /**
     * Get the DLQ topic name.
     */
    public String getDlqTopic() {
        return topic + ".dlq";
    }

    /**
     * Add a header.
     */
    public void addHeader(String key, String value) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.put(key, value);
    }

    /**
     * Check if this is a retried event.
     */
    public boolean isRetry() {
        return retryCount > 0;
    }

    /**
     * Check if this is a DLQ event.
     */
    public boolean isDlq() {
        return dlqReason != null && !dlqReason.isEmpty();
    }

    /**
     * Get correlation ID from payload.
     */
    public String getCorrelationId() {
        return payload != null ? payload.getCorrelationId() : null;
    }

    /**
     * Get event type from payload.
     */
    public String getEventType() {
        return payload != null ? payload.getEventType() : null;
    }
}
