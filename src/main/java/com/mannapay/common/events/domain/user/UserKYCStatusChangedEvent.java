package com.mannapay.common.events.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Event published when user's KYC status changes.
 *
 * KYC status changes affect transaction limits and available features.
 * This event is critical for compliance and must be consumed by all
 * services that enforce transaction limits.
 *
 * Published by: user-service, compliance-service
 * Consumed by: transfer-service, payment-service, compliance-service, notification-service
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
public class UserKYCStatusChangedEvent extends UserEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Profile ID in user-service database.
     */
    private Long profileId;

    /**
     * Previous KYC status.
     * Values: PENDING, IN_PROGRESS, VERIFIED, REJECTED, EXPIRED
     */
    private String previousKycStatus;

    /**
     * New KYC status.
     */
    private String newKycStatus;

    /**
     * Previous KYC level.
     * Values: NONE, BASIC, STANDARD, ENHANCED, FULL
     */
    private String previousKycLevel;

    /**
     * New KYC level.
     */
    private String newKycLevel;

    /**
     * New daily transfer limit based on KYC level.
     */
    private BigDecimal newDailyTransferLimit;

    /**
     * New monthly transfer limit based on KYC level.
     */
    private BigDecimal newMonthlyTransferLimit;

    /**
     * New single transaction limit.
     */
    private BigDecimal newSingleTransactionLimit;

    /**
     * Reason for the status change.
     */
    private String changeReason;

    /**
     * Who triggered the change: USER, ADMIN, SYSTEM, COMPLIANCE
     */
    private String changedBy;

    /**
     * Compliance review ID (if applicable).
     */
    private String complianceReviewId;

    /**
     * Documents submitted for verification (if applicable).
     */
    private String[] documentsSubmitted;

    /**
     * Whether higher limits are now available.
     */
    private boolean limitsIncreased;

    /**
     * Constructor for KYC status change.
     */
    public UserKYCStatusChangedEvent(String keycloakId, String email, Long profileId,
                                      String previousKycStatus, String newKycStatus,
                                      String previousKycLevel, String newKycLevel) {
        initializeUserEvent(keycloakId, email);
        setEventType("UserKYCStatusChanged");
        this.profileId = profileId;
        this.previousKycStatus = previousKycStatus;
        this.newKycStatus = newKycStatus;
        this.previousKycLevel = previousKycLevel;
        this.newKycLevel = newKycLevel;
        this.limitsIncreased = calculateLimitsIncreased(previousKycLevel, newKycLevel);
    }

    private boolean calculateLimitsIncreased(String previousLevel, String newLevel) {
        String[] levels = {"NONE", "BASIC", "STANDARD", "ENHANCED", "FULL"};
        int previousIndex = -1, newIndex = -1;
        for (int i = 0; i < levels.length; i++) {
            if (levels[i].equals(previousLevel)) previousIndex = i;
            if (levels[i].equals(newLevel)) newIndex = i;
        }
        return newIndex > previousIndex;
    }

    @Override
    public String getTopicName() {
        return UserEventTopics.USER_KYC_EVENTS;
    }

    @Override
    public String getEventDescription() {
        return String.format("User KYC changed: %s from %s/%s to %s/%s, limits %s",
            getEmail(), previousKycStatus, previousKycLevel,
            newKycStatus, newKycLevel,
            limitsIncreased ? "increased" : "unchanged/decreased");
    }
}
