package com.mannapay.common.events.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Base class for all domain events in the MannaPay platform.
 *
 * This class provides enterprise-grade event capabilities:
 * - Unique event identification with correlation support
 * - Temporal tracking with high-precision timestamps
 * - Event versioning for schema evolution
 * - Metadata support for tracing and auditing
 * - Causation tracking for event chains
 *
 * All domain events must extend this class to ensure
 * consistent event structure across the platform.
 *
 * @author MannaPay Architecture Team
 * @version 1.0.0
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "eventType",
    visible = true
)
public abstract class DomainEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for this specific event instance.
     * Generated using UUID v4 for guaranteed uniqueness.
     */
    private String eventId;

    /**
     * The type/name of the event (e.g., "TransferInitiated", "PaymentCompleted").
     * Used for routing and polymorphic deserialization.
     */
    private String eventType;

    /**
     * Schema version for event evolution support.
     * Increment when making breaking changes to event structure.
     */
    private int schemaVersion;

    /**
     * Timestamp when the event was created.
     * Uses UTC timezone for consistency across regions.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant timestamp;

    /**
     * The aggregate/entity ID that this event relates to.
     * For transfer events, this would be the transfer ID.
     */
    private String aggregateId;

    /**
     * Type of the aggregate (e.g., "Transfer", "Payment", "User").
     */
    private String aggregateType;

    /**
     * Correlation ID for tracking related events across services.
     * All events in a single business transaction share this ID.
     */
    private String correlationId;

    /**
     * The event ID that caused this event to be generated.
     * Used for building event causation chains.
     */
    private String causationId;

    /**
     * Service/application that produced this event.
     */
    private String source;

    /**
     * User ID associated with this event (if applicable).
     */
    private String userId;

    /**
     * Tenant ID for multi-tenant support.
     */
    private String tenantId;

    /**
     * Sequence number for ordering events within an aggregate.
     * Incremented for each event on the same aggregate.
     */
    private long sequenceNumber;

    /**
     * Additional metadata for extensibility.
     * Can include trace IDs, region info, etc.
     */
    private Map<String, String> metadata;

    /**
     * Initialize default values for the event.
     * Should be called by subclass constructors or event publishers.
     */
    public void initializeDefaults() {
        if (this.eventId == null) {
            this.eventId = UUID.randomUUID().toString();
        }
        if (this.timestamp == null) {
            this.timestamp = Instant.now();
        }
        if (this.schemaVersion == 0) {
            this.schemaVersion = 1;
        }
        if (this.eventType == null) {
            this.eventType = this.getClass().getSimpleName();
        }
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
    }

    /**
     * Get the Kafka topic this event should be published to.
     * Default implementation uses aggregate type as topic prefix.
     */
    public String getTopicName() {
        return String.format("mannapay.%s.events",
            aggregateType != null ? aggregateType.toLowerCase() : "domain");
    }

    /**
     * Get the partition key for Kafka.
     * Default uses aggregate ID for ordering guarantee within aggregate.
     */
    public String getPartitionKey() {
        return aggregateId != null ? aggregateId : eventId;
    }

    /**
     * Add metadata entry.
     */
    public void addMetadata(String key, String value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
    }

    /**
     * Check if this event is part of a correlated chain.
     */
    public boolean isCorrelated() {
        return correlationId != null && !correlationId.isEmpty();
    }

    /**
     * Check if this event has causation information.
     */
    public boolean hasCausation() {
        return causationId != null && !causationId.isEmpty();
    }

    /**
     * Create a child event that inherits correlation from this event.
     * The new event will have this event's ID as its causation ID.
     */
    public void inheritCorrelation(DomainEvent parentEvent) {
        if (parentEvent != null) {
            this.correlationId = parentEvent.getCorrelationId() != null
                ? parentEvent.getCorrelationId()
                : parentEvent.getEventId();
            this.causationId = parentEvent.getEventId();
        }
    }

    /**
     * Get a human-readable description of this event.
     */
    public abstract String getEventDescription();

    /**
     * Validate the event state.
     * @throws IllegalStateException if the event is invalid
     */
    public void validate() {
        if (eventId == null || eventId.isEmpty()) {
            throw new IllegalStateException("Event ID is required");
        }
        if (timestamp == null) {
            throw new IllegalStateException("Timestamp is required");
        }
        if (aggregateId == null || aggregateId.isEmpty()) {
            throw new IllegalStateException("Aggregate ID is required");
        }
    }
}
