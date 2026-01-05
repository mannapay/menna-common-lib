package com.mannapay.common.events.core;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for aggregate roots in Event Sourcing.
 *
 * An aggregate root is the entry point to an aggregate - a cluster of
 * domain objects that are treated as a single unit for data changes.
 *
 * This class provides:
 * - Domain event collection and publishing
 * - Version tracking for optimistic concurrency
 * - Event replay support for reconstituting state
 *
 * Usage pattern:
 * 1. Extend this class for your aggregate root entity
 * 2. Call registerEvent() when state changes
 * 3. Events are collected until publishEvents() is called
 * 4. For reconstitution, call applyEvent() with historical events
 */
public abstract class AggregateRoot {

    /**
     * Current version of the aggregate.
     * Incremented with each event.
     */
    @Getter
    private long version = 0;

    /**
     * Uncommitted events waiting to be published.
     */
    private final List<DomainEvent> uncommittedEvents = new ArrayList<>();

    /**
     * The aggregate's unique identifier.
     */
    public abstract String getAggregateId();

    /**
     * The type name of this aggregate (e.g., "Transfer", "Payment").
     */
    public abstract String getAggregateType();

    /**
     * Register a new event that changes the aggregate state.
     *
     * This method should be called from command handlers when
     * a successful state change occurs.
     *
     * @param event The domain event to register
     */
    protected void registerEvent(DomainEvent event) {
        // Set aggregate information
        event.setAggregateId(getAggregateId());
        event.setAggregateType(getAggregateType());
        event.setSequenceNumber(++version);

        // Apply the event to current state
        applyEvent(event);

        // Add to uncommitted events
        uncommittedEvents.add(event);
    }

    /**
     * Apply an event to update the aggregate state.
     *
     * This method is called both for new events and during
     * event replay for reconstitution.
     *
     * @param event The event to apply
     */
    protected abstract void applyEvent(DomainEvent event);

    /**
     * Get all uncommitted events.
     *
     * @return Unmodifiable list of uncommitted events
     */
    public List<DomainEvent> getUncommittedEvents() {
        return Collections.unmodifiableList(uncommittedEvents);
    }

    /**
     * Clear uncommitted events after successful publish.
     */
    public void clearUncommittedEvents() {
        uncommittedEvents.clear();
    }

    /**
     * Check if there are uncommitted events.
     */
    public boolean hasUncommittedEvents() {
        return !uncommittedEvents.isEmpty();
    }

    /**
     * Get the count of uncommitted events.
     */
    public int getUncommittedEventCount() {
        return uncommittedEvents.size();
    }

    /**
     * Reconstitute aggregate from event history.
     *
     * This method replays events to rebuild aggregate state.
     * Used when loading an aggregate from the event store.
     *
     * @param events List of historical events in order
     */
    public void replayEvents(List<? extends DomainEvent> events) {
        for (DomainEvent event : events) {
            applyEvent(event);
            version = event.getSequenceNumber();
        }
    }

    /**
     * Load aggregate from a snapshot and subsequent events.
     *
     * @param snapshotVersion Version at snapshot time
     * @param events Events after snapshot
     */
    public void loadFromSnapshot(long snapshotVersion, List<? extends DomainEvent> events) {
        this.version = snapshotVersion;
        replayEvents(events);
    }

    /**
     * Check if a snapshot should be created.
     * Default: every 100 events.
     */
    public boolean shouldSnapshot() {
        return version > 0 && version % 100 == 0;
    }
}
