package com.mannapay.common.events.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Event published when a previously deactivated user is reactivated.
 *
 * Published by: auth-service (via admin action)
 * Consumed by: user-service, notification-service
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
public class UserReactivatedEvent extends UserEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Reason for reactivation.
     */
    private String reason;

    /**
     * Who reactivated the account: ADMIN, SYSTEM
     */
    private String reactivatedBy;

    /**
     * Whether KYC verification is required again.
     */
    private boolean requiresKYCReverification;

    /**
     * Additional notes from admin (optional).
     */
    private String adminNotes;

    /**
     * Constructor with essential fields.
     */
    public UserReactivatedEvent(String keycloakId, String email, String reason,
                                 String reactivatedBy) {
        initializeUserEvent(keycloakId, email);
        setEventType("UserReactivated");
        this.reason = reason;
        this.reactivatedBy = reactivatedBy;
        this.requiresKYCReverification = false;
    }

    @Override
    public String getEventDescription() {
        return String.format("User reactivated: %s by %s, reason: %s",
            getEmail(), reactivatedBy, reason);
    }
}
