package com.mannapay.common.events.saga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a single step in a saga.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaStep {

    /**
     * Unique identifier for this step.
     */
    private String stepId;

    /**
     * Name of the step for logging/display.
     */
    private String stepName;

    /**
     * Order of execution (1-based).
     */
    private int order;

    /**
     * Current state of this step.
     */
    private StepState state;

    /**
     * Service responsible for this step.
     */
    private String serviceName;

    /**
     * Command to execute for this step.
     */
    private String command;

    /**
     * Compensation command if rollback is needed.
     */
    private String compensationCommand;

    /**
     * Input data for this step.
     */
    private Map<String, Object> input;

    /**
     * Output data from this step.
     */
    private Map<String, Object> output;

    /**
     * Error message if step failed.
     */
    private String errorMessage;

    /**
     * Time when step started executing.
     */
    private Instant startedAt;

    /**
     * Time when step completed or failed.
     */
    private Instant completedAt;

    /**
     * Number of retry attempts.
     */
    private int retryCount;

    /**
     * Maximum retry attempts allowed.
     */
    private int maxRetries;

    /**
     * Whether compensation was executed.
     */
    private boolean compensated;

    /**
     * Time when compensation was executed.
     */
    private Instant compensatedAt;

    public enum StepState {
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED,
        COMPENSATING,
        COMPENSATED,
        SKIPPED
    }

    /**
     * Mark step as started.
     */
    public void start() {
        this.state = StepState.RUNNING;
        this.startedAt = Instant.now();
    }

    /**
     * Mark step as completed.
     */
    public void complete(Map<String, Object> output) {
        this.state = StepState.COMPLETED;
        this.output = output;
        this.completedAt = Instant.now();
    }

    /**
     * Mark step as failed.
     */
    public void fail(String error) {
        this.state = StepState.FAILED;
        this.errorMessage = error;
        this.completedAt = Instant.now();
    }

    /**
     * Mark compensation as complete.
     */
    public void markCompensated() {
        this.compensated = true;
        this.compensatedAt = Instant.now();
        this.state = StepState.COMPENSATED;
    }

    /**
     * Check if step can be retried.
     */
    public boolean canRetry() {
        return state == StepState.FAILED && retryCount < maxRetries;
    }

    /**
     * Increment retry count.
     */
    public void incrementRetry() {
        this.retryCount++;
        this.state = StepState.PENDING;
    }
}
