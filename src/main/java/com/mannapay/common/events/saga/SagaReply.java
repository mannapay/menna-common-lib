package com.mannapay.common.events.saga;

import com.mannapay.common.events.core.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Reply event from a service after executing a saga command.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SagaReply extends DomainEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Saga instance ID.
     */
    private String sagaId;

    /**
     * Step ID that was executed.
     */
    private String stepId;

    /**
     * Step name (alias for stepId for compatibility).
     */
    private String stepName;

    /**
     * Service that executed the command.
     */
    private String serviceName;

    /**
     * Outcome of the command.
     */
    private Outcome outcome;

    /**
     * Whether the step was successful.
     */
    private boolean success;

    /**
     * Message describing the result.
     */
    private String message;

    /**
     * Result data from command execution.
     */
    private Map<String, Object> resultData;

    /**
     * Error message if failed.
     */
    private String errorMessage;

    /**
     * Error code if failed.
     */
    private String errorCode;

    /**
     * Whether failure is retryable.
     */
    private boolean retryable;

    public enum Outcome {
        SUCCESS,
        FAILURE,
        TIMEOUT
    }

    @Override
    public String getTopicName() {
        return "mannapay.saga.replies";
    }

    @Override
    public String getEventDescription() {
        return String.format("Saga reply: %s from %s (saga: %s, step: %s)",
            outcome, serviceName, sagaId, stepId != null ? stepId : stepName);
    }

    /**
     * Add data to the result data map.
     */
    public void addData(String key, Object value) {
        if (this.resultData == null) {
            this.resultData = new HashMap<>();
        }
        this.resultData.put(key, value);
    }

    /**
     * Create a success reply.
     */
    public static SagaReply success(String sagaId, String stepId, String serviceName, Map<String, Object> data) {
        SagaReply reply = new SagaReply();
        reply.setSagaId(sagaId);
        reply.setStepId(stepId);
        reply.setServiceName(serviceName);
        reply.setOutcome(Outcome.SUCCESS);
        reply.setSuccess(true);
        reply.setResultData(data);
        reply.initializeDefaults();
        return reply;
    }

    /**
     * Create a failure reply.
     */
    public static SagaReply failure(String sagaId, String stepId, String serviceName,
                                    String errorCode, String errorMessage, boolean retryable) {
        SagaReply reply = new SagaReply();
        reply.setSagaId(sagaId);
        reply.setStepId(stepId);
        reply.setServiceName(serviceName);
        reply.setOutcome(Outcome.FAILURE);
        reply.setSuccess(false);
        reply.setErrorCode(errorCode);
        reply.setErrorMessage(errorMessage);
        reply.setRetryable(retryable);
        reply.initializeDefaults();
        return reply;
    }
}
