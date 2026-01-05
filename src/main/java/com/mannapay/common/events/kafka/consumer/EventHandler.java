package com.mannapay.common.events.kafka.consumer;

import com.mannapay.common.events.core.DomainEvent;

/**
 * Functional interface for event handlers.
 *
 * Implement this interface to define business logic for handling
 * specific domain events.
 *
 * @param <T> The type of domain event this handler processes
 */
@FunctionalInterface
public interface EventHandler<T extends DomainEvent> {

    /**
     * Handle the domain event.
     *
     * @param event The event to handle
     * @throws EventHandlerException if processing fails
     */
    void handle(T event) throws EventHandlerException;

    /**
     * Get the event type this handler processes.
     * Default implementation uses reflection.
     */
    default Class<T> getEventType() {
        return null; // Override if needed
    }

    /**
     * Check if this handler can process the given event.
     */
    default boolean canHandle(DomainEvent event) {
        Class<T> eventType = getEventType();
        return eventType == null || eventType.isInstance(event);
    }
}
