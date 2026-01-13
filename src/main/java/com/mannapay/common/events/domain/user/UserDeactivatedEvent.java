package com.mannapay.common.events.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Event published when a user is deactivated.
 *
 * This event triggers cleanup actions across services:
 * - User-service deactivates profile
 * - Payment-service cancels pending transactions
 * - Transfer-service blocks new transfers
 *
 * Published by: auth-service (via admin action or user request)
 * Consumed by: user-service, payment-service, transfer-service, notification-service
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
public class UserDeactivatedEvent extends UserEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Reason for deactivation.
     */
    private String reason;

    /**
     * Who deactivated the account: USER, ADMIN, SYSTEM, COMPLIANCE
     */
    private String deactivatedBy;

    /**
     * Whether this is a permanent deletion or temporary deactivation.
     */
    private boolean permanent;

    /**
     * Whether pending transactions should be cancelled.
     */
    private boolean cancelPendingTransactions;

    /**
     * Compliance case ID if deactivated due to compliance issue.
     */
    private String complianceCaseId;

    /**
     * Additional notes from admin (optional).
     */
    private String adminNotes;

    /**
     * Constructor with essential fields.
     */
    public UserDeactivatedEvent(String keycloakId, String email, String reason,
                                 String deactivatedBy) {
        initializeUserEvent(keycloakId, email);
        setEventType("UserDeactivated");
        this.reason = reason;
        this.deactivatedBy = deactivatedBy;
        this.permanent = false;
        this.cancelPendingTransactions = true;
    }

    @Override
    public String getEventDescription() {
        return String.format("User deactivated: %s by %s, reason: %s, permanent: %s",
            getEmail(), deactivatedBy, reason, permanent);
    }
}
