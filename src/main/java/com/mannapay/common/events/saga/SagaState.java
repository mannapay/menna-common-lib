package com.mannapay.common.events.saga;

/**
 * Possible states of a saga.
 */
public enum SagaState {
    /**
     * Saga has been created but not started.
     */
    CREATED,

    /**
     * Saga is executing forward steps.
     */
    RUNNING,

    /**
     * Saga completed successfully.
     */
    COMPLETED,

    /**
     * Saga is executing compensation (rollback).
     */
    COMPENSATING,

    /**
     * Saga compensation completed.
     */
    COMPENSATED,

    /**
     * Saga failed and cannot be compensated.
     */
    FAILED,

    /**
     * Saga is suspended waiting for manual intervention.
     */
    SUSPENDED
}
