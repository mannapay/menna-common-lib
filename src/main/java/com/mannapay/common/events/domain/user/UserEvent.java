package com.mannapay.common.events.domain.user;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mannapay.common.events.core.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Base class for all user-related domain events.
 *
 * User events capture all state changes in the user lifecycle:
 * - Registration and profile creation
 * - Profile updates and verification
 * - KYC status changes
 * - Account activation/deactivation
 * - Login/logout activities (audit)
 *
 * These events form the audit trail required for financial compliance
 * and enable event-driven communication between auth-service and user-service.
 *
 * Published by: auth-service (authentication events), user-service (profile events)
 * Consumed by: user-service, notification-service, fraud-detection-service, audit-service
 *
 * @author MannaPay Architecture Team
 * @version 1.0.0
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "eventType",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = UserRegisteredEvent.class, name = "UserRegistered"),
    @JsonSubTypes.Type(value = UserProfileCreatedEvent.class, name = "UserProfileCreated"),
    @JsonSubTypes.Type(value = UserProfileUpdatedEvent.class, name = "UserProfileUpdated"),
    @JsonSubTypes.Type(value = UserDeactivatedEvent.class, name = "UserDeactivated"),
    @JsonSubTypes.Type(value = UserReactivatedEvent.class, name = "UserReactivated"),
    @JsonSubTypes.Type(value = UserKYCStatusChangedEvent.class, name = "UserKYCStatusChanged"),
    @JsonSubTypes.Type(value = UserLoginEvent.class, name = "UserLogin"),
    @JsonSubTypes.Type(value = UserPasswordChangedEvent.class, name = "UserPasswordChanged"),
    @JsonSubTypes.Type(value = UserEmailVerifiedEvent.class, name = "UserEmailVerified")
})
public abstract class UserEvent extends DomainEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Keycloak user ID - the universal identifier across services.
     * This is the primary key for user identification in the distributed system.
     */
    private String keycloakId;

    /**
     * User's email address.
     * Used for notifications and as a secondary identifier.
     */
    private String email;

    /**
     * Source service that generated this event.
     * Examples: "auth-service", "user-service", "admin-portal"
     */
    private String sourceService;

    /**
     * IP address from which the action was performed (for audit).
     */
    private String ipAddress;

    /**
     * User agent string (for audit/security).
     */
    private String userAgent;

    /**
     * Device ID if from mobile app (for audit/security).
     */
    private String deviceId;

    @Override
    public String getTopicName() {
        return UserEventTopics.USER_EVENTS;
    }

    @Override
    public String getPartitionKey() {
        // Partition by keycloakId for ordering within a user
        return keycloakId != null ? keycloakId : super.getPartitionKey();
    }

    /**
     * Initialize with aggregate information.
     */
    protected void initializeUserEvent(String keycloakId, String email) {
        this.keycloakId = keycloakId;
        this.email = email;
        setAggregateId(keycloakId);
        setAggregateType("User");
        initializeDefaults();
    }
}
