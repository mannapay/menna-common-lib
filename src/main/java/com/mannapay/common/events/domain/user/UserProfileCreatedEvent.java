package com.mannapay.common.events.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Event published when user profile is created in user-service.
 *
 * This event confirms that the user profile was successfully created
 * after receiving UserRegisteredEvent from auth-service.
 *
 * Published by: user-service
 * Consumed by: notification-service
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
public class UserProfileCreatedEvent extends UserEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Profile ID in user-service database.
     */
    private Long profileId;

    /**
     * User's first name.
     */
    private String firstName;

    /**
     * User's last name.
     */
    private String lastName;

    /**
     * Initial KYC status assigned.
     */
    private String kycStatus;

    /**
     * Initial KYC level assigned.
     */
    private String kycLevel;

    /**
     * Initial account tier assigned.
     */
    private String accountTier;

    /**
     * Initial daily transfer limit.
     */
    private Double dailyTransferLimit;

    /**
     * Initial monthly transfer limit.
     */
    private Double monthlyTransferLimit;

    /**
     * Constructor with essential fields.
     */
    public UserProfileCreatedEvent(String keycloakId, String email, Long profileId,
                                    String firstName, String lastName) {
        initializeUserEvent(keycloakId, email);
        setEventType("UserProfileCreated");
        this.profileId = profileId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.kycStatus = "PENDING";
        this.kycLevel = "NONE";
        this.accountTier = "BASIC";
    }

    @Override
    public String getEventDescription() {
        return String.format("User profile created: profileId=%d for %s %s (%s)",
            profileId, firstName, lastName, getEmail());
    }
}
