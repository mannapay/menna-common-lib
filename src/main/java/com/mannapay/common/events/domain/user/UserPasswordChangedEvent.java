package com.mannapay.common.events.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Event published when user changes or resets their password.
 *
 * This event is important for security monitoring and notification purposes.
 *
 * Published by: auth-service
 * Consumed by: notification-service, audit-service
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
public class UserPasswordChangedEvent extends UserEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Type of password change.
     * Values: USER_CHANGED, ADMIN_RESET, FORGOT_PASSWORD, FORCED_RESET
     */
    private String changeType;

    /**
     * Who initiated the change: USER, ADMIN, SYSTEM
     */
    private String changedBy;

    /**
     * Whether all existing sessions were invalidated.
     */
    private boolean sessionsInvalidated;

    /**
     * Number of sessions invalidated (if applicable).
     */
    private int sessionsInvalidatedCount;

    /**
     * Whether the user was notified via email.
     */
    private boolean emailNotificationSent;

    /**
     * Whether the password change was due to security policy (e.g., password expired).
     */
    private boolean policyEnforced;

    /**
     * Constructor for user-initiated password change.
     */
    public UserPasswordChangedEvent(String keycloakId, String email, String changeType,
                                     String changedBy, boolean sessionsInvalidated) {
        initializeUserEvent(keycloakId, email);
        setEventType("UserPasswordChanged");
        this.changeType = changeType;
        this.changedBy = changedBy;
        this.sessionsInvalidated = sessionsInvalidated;
        this.emailNotificationSent = true;
    }

    @Override
    public String getTopicName() {
        return UserEventTopics.USER_LOGIN_EVENTS;
    }

    @Override
    public String getEventDescription() {
        return String.format("User password changed: %s, type: %s, by: %s, sessions invalidated: %s",
            getEmail(), changeType, changedBy, sessionsInvalidated);
    }
}
