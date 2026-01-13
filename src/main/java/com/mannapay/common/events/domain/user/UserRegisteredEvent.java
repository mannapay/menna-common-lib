package com.mannapay.common.events.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Event published when a new user registers in the system.
 *
 * This is the first event in the user lifecycle and triggers:
 * - Profile creation in user-service
 * - Welcome notification
 * - Initial KYC status setup
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
public class UserRegisteredEvent extends UserEvent {

    private static final long serialVersionUID = 1L;

    /**
     * User's first name.
     */
    private String firstName;

    /**
     * User's last name.
     */
    private String lastName;

    /**
     * User's phone number (optional).
     */
    private String phone;

    /**
     * Registration source: WEB, MOBILE_IOS, MOBILE_ANDROID, ADMIN
     */
    private String registrationSource;

    /**
     * Initial roles assigned to the user.
     * Default: ["USER"]
     */
    private String[] roles;

    /**
     * Whether email verification is required.
     */
    private boolean emailVerificationRequired;

    /**
     * Referral code used during registration (if any).
     */
    private String referralCode;

    /**
     * Marketing consent status.
     */
    private boolean marketingConsentGiven;

    /**
     * Constructor with essential fields.
     */
    public UserRegisteredEvent(String keycloakId, String email, String firstName,
                                String lastName, String phone, String registrationSource) {
        initializeUserEvent(keycloakId, email);
        setEventType("UserRegistered");
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.registrationSource = registrationSource;
        this.roles = new String[]{"USER"};
        this.emailVerificationRequired = true;
    }

    @Override
    public String getEventDescription() {
        return String.format("User registered: %s %s (%s) via %s",
            firstName, lastName, getEmail(), registrationSource);
    }
}
