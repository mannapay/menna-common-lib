package com.mannapay.common.events.kafka.consumer;

/**
 * Exception thrown when event handling fails.
 */
public class EventHandlerException extends RuntimeException {

    private final boolean retryable;
    private final String eventId;
    private final String eventType;

    public EventHandlerException(String message) {
        super(message);
        this.retryable = true;
        this.eventId = null;
        this.eventType = null;
    }

    public EventHandlerException(String message, Throwable cause) {
        super(message, cause);
        this.retryable = true;
        this.eventId = null;
        this.eventType = null;
    }

    public EventHandlerException(String message, Throwable cause, boolean retryable) {
        super(message, cause);
        this.retryable = retryable;
        this.eventId = null;
        this.eventType = null;
    }

    public EventHandlerException(String message, String eventId, String eventType, boolean retryable) {
        super(message);
        this.eventId = eventId;
        this.eventType = eventType;
        this.retryable = retryable;
    }

    public EventHandlerException(String message, Throwable cause, String eventId, String eventType, boolean retryable) {
        super(message, cause);
        this.eventId = eventId;
        this.eventType = eventType;
        this.retryable = retryable;
    }

    public boolean isRetryable() {
        return retryable;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    /**
     * Create a non-retryable exception.
     */
    public static EventHandlerException nonRetryable(String message, Throwable cause) {
        return new EventHandlerException(message, cause, false);
    }

    /**
     * Create a retryable exception.
     */
    public static EventHandlerException retryable(String message, Throwable cause) {
        return new EventHandlerException(message, cause, true);
    }
}
