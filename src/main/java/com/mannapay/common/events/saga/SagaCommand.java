package com.mannapay.common.events.saga;

import com.mannapay.common.events.core.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.Map;

/**
 * Command event sent to a service as part of a saga.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SagaCommand extends DomainEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Saga instance ID.
     */
    private String sagaId;

    /**
     * Type of saga.
     */
    private String sagaType;

    /**
     * Step ID within the saga.
     */
    private String stepId;

    /**
     * Command name.
     */
    private String commandName;

    /**
     * Target service.
     */
    private String targetService;

    /**
     * Command payload.
     */
    private Map<String, Object> payload;

    /**
     * Whether this is a compensation command.
     */
    private boolean compensation;

    /**
     * Reply topic for response.
     */
    private String replyTopic;

    @Override
    public String getTopicName() {
        return "mannapay.saga.commands." + targetService.toLowerCase();
    }

    @Override
    public String getEventDescription() {
        return String.format("Saga command: %s to %s (saga: %s, step: %s)",
            commandName, targetService, sagaId, stepId);
    }
}
