package com.mannapay.common.events.saga;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Entity representing a saga instance.
 *
 * A saga orchestrates a distributed transaction across multiple services.
 * Each saga instance tracks the current state, completed steps, and
 * compensation steps needed for rollback.
 */
@Entity
@Table(name = "saga_instances",
    indexes = {
        @Index(name = "idx_saga_state", columnList = "state"),
        @Index(name = "idx_saga_type", columnList = "saga_type"),
        @Index(name = "idx_saga_correlation", columnList = "correlation_id"),
        @Index(name = "idx_saga_created", columnList = "created_at")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Type of saga (e.g., "TransferSaga", "PaymentSaga").
     */
    @Column(name = "saga_type", nullable = false, length = 100)
    private String sagaType;

    /**
     * Correlation ID for tracing related events.
     */
    @Column(name = "correlation_id", nullable = false, length = 36)
    private String correlationId;

    /**
     * Current state of the saga.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    @Builder.Default
    private SagaState state = SagaState.CREATED;

    /**
     * Current step index (0-based).
     */
    @Column(name = "current_step")
    @Builder.Default
    private int currentStep = 0;

    /**
     * Steps in this saga.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "steps", columnDefinition = "jsonb")
    @Builder.Default
    private List<SagaStep> steps = new ArrayList<>();

    /**
     * Saga input data.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "input_data", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> inputData = new HashMap<>();

    /**
     * Saga output data (accumulated from steps).
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "output_data", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> outputData = new HashMap<>();

    /**
     * Error that caused saga failure.
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Step where failure occurred.
     */
    @Column(name = "failed_step")
    private Integer failedStep;

    /**
     * Service that initiated the saga.
     */
    @Column(name = "initiator_service", length = 100)
    private String initiatorService;

    /**
     * User who initiated the saga.
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Time when saga was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    /**
     * Time when saga started executing.
     */
    @Column(name = "started_at")
    private Instant startedAt;

    /**
     * Time when saga completed/failed.
     */
    @Column(name = "completed_at")
    private Instant completedAt;

    /**
     * Time of last update.
     */
    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    /**
     * Optimistic locking version.
     */
    @Version
    private Long version;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    /**
     * Start saga execution.
     */
    public void start() {
        this.state = SagaState.RUNNING;
        this.startedAt = Instant.now();
    }

    /**
     * Mark saga as completed.
     */
    public void complete() {
        this.state = SagaState.COMPLETED;
        this.completedAt = Instant.now();
    }

    /**
     * Mark saga as failed.
     */
    public void fail(String error, int failedStep) {
        this.state = SagaState.FAILED;
        this.errorMessage = error;
        this.failedStep = failedStep;
        this.completedAt = Instant.now();
    }

    /**
     * Start compensation.
     */
    public void startCompensation() {
        this.state = SagaState.COMPENSATING;
    }

    /**
     * Mark compensation as complete.
     */
    public void completeCompensation() {
        this.state = SagaState.COMPENSATED;
        this.completedAt = Instant.now();
    }

    /**
     * Suspend saga for manual intervention.
     */
    public void suspend(String reason) {
        this.state = SagaState.SUSPENDED;
        this.errorMessage = reason;
    }

    /**
     * Get current step.
     */
    public SagaStep getCurrentStepInfo() {
        if (currentStep >= 0 && currentStep < steps.size()) {
            return steps.get(currentStep);
        }
        return null;
    }

    /**
     * Move to next step.
     */
    public boolean nextStep() {
        if (currentStep < steps.size() - 1) {
            currentStep++;
            return true;
        }
        return false;
    }

    /**
     * Check if saga is in terminal state.
     */
    public boolean isTerminal() {
        return state == SagaState.COMPLETED ||
               state == SagaState.COMPENSATED ||
               state == SagaState.FAILED;
    }

    /**
     * Add output from a step.
     */
    public void addOutput(String key, Object value) {
        if (outputData == null) {
            outputData = new HashMap<>();
        }
        outputData.put(key, value);
    }

    /**
     * Get completed steps for compensation.
     */
    public List<SagaStep> getCompletedSteps() {
        return steps.stream()
            .filter(s -> s.getState() == SagaStep.StepState.COMPLETED)
            .toList();
    }

    /**
     * Calculate saga duration in milliseconds.
     */
    public Long getDurationMs() {
        if (startedAt == null) {
            return null;
        }
        Instant end = completedAt != null ? completedAt : Instant.now();
        return end.toEpochMilli() - startedAt.toEpochMilli();
    }
}
