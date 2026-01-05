package com.mannapay.common.model.enums;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * KYC verification levels with associated transaction limits.
 */
@Getter
public enum KYCLevel {
    UNVERIFIED(
            "Unverified",
            "No verification",
            new BigDecimal("0"),
            new BigDecimal("0"),
            new BigDecimal("0")
    ),
    BASIC(
            "Basic",
            "Basic verification (phone/email)",
            new BigDecimal("500"),
            new BigDecimal("2000"),
            new BigDecimal("5000")
    ),
    STANDARD(
            "Standard",
            "Standard verification (ID document)",
            new BigDecimal("5000"),
            new BigDecimal("20000"),
            new BigDecimal("50000")
    ),
    ENHANCED(
            "Enhanced",
            "Enhanced verification (ID + proof of address)",
            new BigDecimal("50000"),
            new BigDecimal("200000"),
            new BigDecimal("500000")
    );

    private final String displayName;
    private final String description;
    private final BigDecimal singleTransactionLimit;
    private final BigDecimal dailyLimit;
    private final BigDecimal monthlyLimit;

    KYCLevel(String displayName, String description,
             BigDecimal singleTransactionLimit,
             BigDecimal dailyLimit,
             BigDecimal monthlyLimit) {
        this.displayName = displayName;
        this.description = description;
        this.singleTransactionLimit = singleTransactionLimit;
        this.dailyLimit = dailyLimit;
        this.monthlyLimit = monthlyLimit;
    }

    /**
     * Check if amount is within single transaction limit
     */
    public boolean isWithinSingleTransactionLimit(BigDecimal amount) {
        return amount.compareTo(singleTransactionLimit) <= 0;
    }

    /**
     * Check if amount is within daily limit
     */
    public boolean isWithinDailyLimit(BigDecimal amount) {
        return amount.compareTo(dailyLimit) <= 0;
    }

    /**
     * Check if amount is within monthly limit
     */
    public boolean isWithinMonthlyLimit(BigDecimal amount) {
        return amount.compareTo(monthlyLimit) <= 0;
    }

    /**
     * Get next KYC level for upgrade
     */
    public KYCLevel getNextLevel() {
        return switch (this) {
            case UNVERIFIED -> BASIC;
            case BASIC -> STANDARD;
            case STANDARD -> ENHANCED;
            case ENHANCED -> null; // Maximum level
        };
    }

    /**
     * Check if this level can be upgraded
     */
    public boolean canUpgrade() {
        return this != ENHANCED;
    }
}
