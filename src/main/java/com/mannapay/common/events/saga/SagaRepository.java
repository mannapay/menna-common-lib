package com.mannapay.common.events.saga;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for saga instances.
 */
@Repository
public interface SagaRepository extends JpaRepository<SagaInstance, UUID> {

    /**
     * Find saga by correlation ID.
     */
    Optional<SagaInstance> findByCorrelationId(String correlationId);

    /**
     * Find running sagas.
     */
    List<SagaInstance> findByState(SagaState state);

    /**
     * Find sagas by type and state.
     */
    List<SagaInstance> findBySagaTypeAndState(String sagaType, SagaState state);

    /**
     * Find sagas for a user.
     */
    List<SagaInstance> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find stuck sagas (running for too long).
     */
    @Query("SELECT s FROM SagaInstance s WHERE s.state = 'RUNNING' AND s.startedAt < :cutoff")
    List<SagaInstance> findStuckSagas(@Param("cutoff") Instant cutoff);

    /**
     * Find sagas that need compensation.
     */
    @Query("SELECT s FROM SagaInstance s WHERE s.state = 'COMPENSATING' ORDER BY s.updatedAt ASC")
    List<SagaInstance> findSagasNeedingCompensation();

    /**
     * Count sagas by state.
     */
    long countByState(SagaState state);

    /**
     * Find suspended sagas.
     */
    List<SagaInstance> findByStateOrderByCreatedAtDesc(SagaState state);
}
