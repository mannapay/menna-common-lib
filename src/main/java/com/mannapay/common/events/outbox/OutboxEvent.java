package com.mannapay.common.events.outbox;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Outbox event entity for the Transactional Outbox Pattern.
 *
 * The Transactional Outbox Pattern ensures reliable event publishing
 * by storing events in the same database transaction as the business
 * operation, then asynchronously publishing to Kafka.
 *
 * This guarantees at-least-once delivery even if the application
 * crashes after committing the database transaction but before
 * publishing to Kafka.
 *
 * States:
 * - PENDING: Event is waiting to be published
 * - PUBLISHED: Event has been successfully published to Kafka
 * - FAILED: Event failed to publish after max retries
 */
@Entity
@Table(name = "outbox_events",
    indexes = {
        @Index(name = "idx_outbox_status", columnList = "status"),
        @Index(name = "idx_outbox_created_at", columnList = "created_at"),
        @Index(name = "idx_outbox_aggregate", columnList = "aggregate_type, aggregate_id"),
        @Index(name = "idx_outbox_topic", columnList = "topic")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "topic", nullable = false, length = 255)
    private String topic;

    @Column(name = "partition_key", length = 255)
    private String partitionKey;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "correlation_id", length = 36)
    private String correlationId;

    @Column(name = "causation_id", length = 36)
    private String causationId;

    @Column(name = "trace_id", length = 36)
    private String traceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private OutboxStatus status = OutboxStatus.PENDING;

    @Column(name = "retry_count")
    @Builder.Default
    private int retryCount = 0;

    @Column(name = "max_retries")
    @Builder.Default
    private int maxRetries = 5;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "next_retry_at")
    private Instant nextRetryAt;

    public enum OutboxStatus {
        PENDING,
        PUBLISHED,
        FAILED
    }

    /**
     * Mark as published.
     */
    public void markPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.processedAt = Instant.now();
    }

    /**
     * Record a failed attempt.
     */
    public void recordFailure(String error) {
        this.retryCount++;
        this.lastError = error;

        if (retryCount >= maxRetries) {
            this.status = OutboxStatus.FAILED;
        } else {
            // Exponential backoff: 1s, 2s, 4s, 8s, 16s
            long delaySeconds = (long) Math.pow(2, retryCount);
            this.nextRetryAt = Instant.now().plusSeconds(delaySeconds);
        }
    }

    /**
     * Check if should retry.
     */
    public boolean shouldRetry() {
        return status == OutboxStatus.PENDING &&
               retryCount < maxRetries &&
               (nextRetryAt == null || Instant.now().isAfter(nextRetryAt));
    }
}
