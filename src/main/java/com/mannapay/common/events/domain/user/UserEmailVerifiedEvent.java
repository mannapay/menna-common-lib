package com.mannapay.common.events.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Event published when user's email is verified.
 *
 * Email verification is a prerequisite for certain features and
 * may upgrade the user's KYC level.
 *
 * Published by: auth-service
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
public class UserEmailVerifiedEvent extends UserEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Verification method used.
     * Values: LINK, CODE, ADMIN_VERIFIED
     */
    private String verificationMethod;

    /**
     * Whether this triggers KYC level upgrade.
     */
    private boolean triggersKYCUpgrade;

    /**
     * New KYC level after verification (if upgraded).
     */
    private String newKycLevel;

    /**
     * Time taken to verify from registration (in seconds).
     */
    private long verificationDurationSeconds;

    /**
     * Number of verification attempts before success.
     */
    private int attemptCount;

    /**
     * Constructor with essential fields.
     */
    public UserEmailVerifiedEvent(String keycloakId, String email, String verificationMethod) {
        initializeUserEvent(keycloakId, email);
        setEventType("UserEmailVerified");
        this.verificationMethod = verificationMethod;
        this.triggersKYCUpgrade = true;
        this.attemptCount = 1;
    }

    @Override
    public String getTopicName() {
        return UserEventTopics.USER_KYC_EVENTS;
    }

    @Override
    public String getEventDescription() {
        return String.format("User email verified: %s via %s, KYC upgrade: %s",
            getEmail(), verificationMethod, triggersKYCUpgrade);
    }
}
