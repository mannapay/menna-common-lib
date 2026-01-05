package com.mannapay.common.events.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mannapay.common.events.core.DomainEvent;
import com.mannapay.common.events.kafka.producer.EventPublisher;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for managing the transactional outbox.
 *
 * This service provides methods to:
 * - Store events in the outbox within a database transaction
 * - Process pending events and publish to Kafka
 * - Handle retries with exponential backoff
 * - Clean up old published events
 *
 * Usage:
 * 1. Call saveEvent() within your @Transactional business method
 * 2. The poller will automatically publish events to Kafka
 */
@Service
@Slf4j
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;

    private final Counter eventsSavedCounter;
    private final Counter eventsPublishedCounter;
    private final Counter eventsFailedCounter;
    private final AtomicLong pendingEventsGauge = new AtomicLong(0);

    private static final int BATCH_SIZE = 100;
    private static final Duration CLEANUP_AGE = Duration.ofDays(7);

    public OutboxService(
            OutboxRepository outboxRepository,
            EventPublisher eventPublisher,
            ObjectMapper objectMapper,
            MeterRegistry meterRegistry) {

        this.outboxRepository = outboxRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
        this.meterRegistry = meterRegistry;

        // Initialize metrics
        this.eventsSavedCounter = Counter.builder("mannapay.outbox.events.saved")
            .description("Events saved to outbox")
            .register(meterRegistry);

        this.eventsPublishedCounter = Counter.builder("mannapay.outbox.events.published")
            .description("Events published from outbox")
            .register(meterRegistry);

        this.eventsFailedCounter = Counter.builder("mannapay.outbox.events.failed")
            .description("Events failed to publish from outbox")
            .register(meterRegistry);

        Gauge.builder("mannapay.outbox.events.pending", pendingEventsGauge, AtomicLong::get)
            .description("Number of pending events in outbox")
            .register(meterRegistry);
    }

    /**
     * Save a domain event to the outbox.
     *
     * Call this within your @Transactional method to ensure
     * the event is stored atomically with your business operation.
     *
     * @param event The domain event to save
     * @return The created outbox event ID
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public UUID saveEvent(DomainEvent event) {
        event.initializeDefaults();

        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new OutboxException("Failed to serialize event: " + event.getEventType(), e);
        }

        OutboxEvent outboxEvent = OutboxEvent.builder()
            .aggregateType(event.getAggregateType())
            .aggregateId(event.getAggregateId())
            .eventType(event.getEventType())
            .topic(event.getTopicName())
            .partitionKey(event.getPartitionKey())
            .payload(payload)
            .correlationId(event.getCorrelationId())
            .causationId(event.getCausationId())
            .status(OutboxEvent.OutboxStatus.PENDING)
            .build();

        outboxEvent = outboxRepository.save(outboxEvent);
        eventsSavedCounter.increment();

        log.debug("Event saved to outbox: id={}, type={}, aggregateId={}",
            outboxEvent.getId(), event.getEventType(), event.getAggregateId());

        return outboxEvent.getId();
    }

    /**
     * Save a domain event to the outbox with a specific topic.
     * Alias for saveEvent() for backward compatibility.
     *
     * @param event The domain event to save
     * @param topic The topic to publish to (stored in the event)
     * @return The created outbox event ID
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public UUID saveToOutbox(DomainEvent event, String topic) {
        // The topic is already determined by the event's getTopicName() method
        // but we can override the aggregate type if needed
        return saveEvent(event);
    }

    /**
     * Save multiple events to the outbox.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void saveEvents(List<DomainEvent> events) {
        for (DomainEvent event : events) {
            saveEvent(event);
        }
    }

    /**
     * Process pending outbox events.
     * Called by the scheduler every second.
     */
    @Scheduled(fixedDelayString = "${outbox.poll-interval-ms:1000}")
    @Transactional
    public void processPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxRepository.findPendingEventsWithLimit(
            Instant.now(), BATCH_SIZE);

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.debug("Processing {} pending outbox events", pendingEvents.size());

        for (OutboxEvent outboxEvent : pendingEvents) {
            processEvent(outboxEvent);
        }

        // Update pending gauge
        pendingEventsGauge.set(outboxRepository.countByStatus(OutboxEvent.OutboxStatus.PENDING));
    }

    /**
     * Process a single outbox event.
     */
    private void processEvent(OutboxEvent outboxEvent) {
        try {
            DomainEvent event = deserializeEvent(outboxEvent);

            eventPublisher.publish(event, outboxEvent.getTopic())
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        handlePublishFailure(outboxEvent, ex);
                    } else {
                        handlePublishSuccess(outboxEvent);
                    }
                });

        } catch (Exception e) {
            handlePublishFailure(outboxEvent, e);
        }
    }

    /**
     * Deserialize the event from JSON.
     */
    @SuppressWarnings("unchecked")
    private DomainEvent deserializeEvent(OutboxEvent outboxEvent) {
        try {
            return objectMapper.readValue(outboxEvent.getPayload(), DomainEvent.class);
        } catch (JsonProcessingException e) {
            throw new OutboxException("Failed to deserialize event: " + outboxEvent.getId(), e);
        }
    }

    /**
     * Handle successful publish.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePublishSuccess(OutboxEvent outboxEvent) {
        outboxEvent.markPublished();
        outboxRepository.save(outboxEvent);
        eventsPublishedCounter.increment();

        log.debug("Outbox event published: id={}, type={}",
            outboxEvent.getId(), outboxEvent.getEventType());
    }

    /**
     * Handle publish failure.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePublishFailure(OutboxEvent outboxEvent, Throwable error) {
        outboxEvent.recordFailure(error.getMessage());
        outboxRepository.save(outboxEvent);

        if (outboxEvent.getStatus() == OutboxEvent.OutboxStatus.FAILED) {
            eventsFailedCounter.increment();
            log.error("Outbox event permanently failed after {} retries: id={}, type={}, error={}",
                outboxEvent.getRetryCount(),
                outboxEvent.getId(),
                outboxEvent.getEventType(),
                error.getMessage());
        } else {
            log.warn("Outbox event publish failed, will retry: id={}, attempt={}, error={}",
                outboxEvent.getId(),
                outboxEvent.getRetryCount(),
                error.getMessage());
        }
    }

    /**
     * Clean up old published events.
     * Runs daily at midnight.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupOldEvents() {
        Instant cutoff = Instant.now().minus(CLEANUP_AGE);
        int deleted = outboxRepository.deleteOldPublishedEvents(cutoff);

        if (deleted > 0) {
            log.info("Cleaned up {} old outbox events", deleted);
        }
    }

    /**
     * Retry a failed event manually.
     */
    @Transactional
    public void retryFailedEvent(UUID eventId) {
        OutboxEvent event = outboxRepository.findById(eventId)
            .orElseThrow(() -> new OutboxException("Outbox event not found: " + eventId));

        if (event.getStatus() != OutboxEvent.OutboxStatus.FAILED) {
            throw new OutboxException("Event is not in FAILED status: " + eventId);
        }

        event.setStatus(OutboxEvent.OutboxStatus.PENDING);
        event.setRetryCount(0);
        event.setNextRetryAt(null);
        outboxRepository.save(event);

        log.info("Reset failed outbox event for retry: id={}", eventId);
    }

    /**
     * Get pending event count.
     */
    public long getPendingCount() {
        return outboxRepository.countByStatus(OutboxEvent.OutboxStatus.PENDING);
    }

    /**
     * Get failed event count.
     */
    public long getFailedCount() {
        return outboxRepository.countByStatus(OutboxEvent.OutboxStatus.FAILED);
    }

    /**
     * Custom exception for outbox operations.
     */
    public static class OutboxException extends RuntimeException {
        public OutboxException(String message) {
            super(message);
        }

        public OutboxException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
