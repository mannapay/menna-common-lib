package com.mannapay.common.events.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository for outbox events.
 */
@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {

    /**
     * Find pending events ready for publishing.
     */
    @Query("SELECT o FROM OutboxEvent o WHERE o.status = 'PENDING' " +
           "AND (o.nextRetryAt IS NULL OR o.nextRetryAt <= :now) " +
           "ORDER BY o.createdAt ASC")
    List<OutboxEvent> findPendingEvents(@Param("now") Instant now);

    /**
     * Find pending events with limit.
     */
    @Query(value = "SELECT * FROM outbox_events o WHERE o.status = 'PENDING' " +
                   "AND (o.next_retry_at IS NULL OR o.next_retry_at <= :now) " +
                   "ORDER BY o.created_at ASC LIMIT :limit FOR UPDATE SKIP LOCKED",
           nativeQuery = true)
    List<OutboxEvent> findPendingEventsWithLimit(
        @Param("now") Instant now,
        @Param("limit") int limit);

    /**
     * Find pending events by aggregate.
     */
    List<OutboxEvent> findByAggregateTypeAndAggregateIdAndStatus(
        String aggregateType,
        String aggregateId,
        OutboxEvent.OutboxStatus status);

    /**
     * Find failed events.
     */
    List<OutboxEvent> findByStatusOrderByCreatedAtDesc(OutboxEvent.OutboxStatus status);

    /**
     * Delete old published events.
     */
    @Modifying
    @Query("DELETE FROM OutboxEvent o WHERE o.status = 'PUBLISHED' AND o.processedAt < :cutoff")
    int deleteOldPublishedEvents(@Param("cutoff") Instant cutoff);

    /**
     * Count pending events.
     */
    long countByStatus(OutboxEvent.OutboxStatus status);

    /**
     * Find events by correlation ID.
     */
    List<OutboxEvent> findByCorrelationIdOrderByCreatedAtAsc(String correlationId);
}
