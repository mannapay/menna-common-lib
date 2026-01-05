package com.mannapay.common.model.enums;

import lombok.Getter;

/**
 * Types of recipients for money transfers.
 */
@Getter
public enum RecipientType {
    BANK_ACCOUNT("Bank account", "Direct bank deposit"),
    MOBILE_WALLET("Mobile wallet", "Mobile money wallet"),
    CARD_HOLDER("Card holder", "Direct to debit/credit card"),
    CASH_PICKUP("Cash pickup", "Cash collection at agent location"),
    HOME_DELIVERY("Home delivery", "Cash delivery to recipient address");

    private final String displayName;
    private final String description;

    RecipientType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Check if recipient type requires bank details
     */
    public boolean requiresBankDetails() {
        return this == BANK_ACCOUNT;
    }

    /**
     * Check if recipient type requires card details
     */
    public boolean requiresCardDetails() {
        return this == CARD_HOLDER;
    }

    /**
     * Check if recipient type requires mobile number
     */
    public boolean requiresMobileNumber() {
        return this == MOBILE_WALLET || this == CASH_PICKUP;
    }

    /**
     * Check if recipient type requires physical address
     */
    public boolean requiresAddress() {
        return this == HOME_DELIVERY || this == CASH_PICKUP;
    }

    /**
     * Check if recipient type provides instant transfer
     */
    public boolean isInstant() {
        return this == MOBILE_WALLET || this == CARD_HOLDER;
    }
}
