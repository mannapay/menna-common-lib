package com.mannapay.common.model.enums;

import lombok.Getter;

/**
 * Types of transactions in the system.
 */
@Getter
public enum TransactionType {
    TRANSFER_SENT("Transfer sent", "Money sent to recipient"),
    TRANSFER_RECEIVED("Transfer received", "Money received from sender"),
    PAYMENT("Payment", "Payment for transfer fees"),
    REFUND("Refund", "Refund of payment/transfer"),
    FEE("Fee", "Service fee charge"),
    COMMISSION("Commission", "Agent commission"),
    ADJUSTMENT("Adjustment", "Manual adjustment by admin"),
    WALLET_TOPUP("Wallet top-up", "Wallet balance top-up"),
    WALLET_WITHDRAWAL("Wallet withdrawal", "Wallet balance withdrawal");

    private final String displayName;
    private final String description;

    TransactionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Check if transaction type is a debit (reduces balance)
     */
    public boolean isDebit() {
        return this == TRANSFER_SENT || this == PAYMENT || this == FEE || this == WALLET_WITHDRAWAL;
    }

    /**
     * Check if transaction type is a credit (increases balance)
     */
    public boolean isCredit() {
        return this == TRANSFER_RECEIVED || this == REFUND || this == COMMISSION ||
               this == WALLET_TOPUP;
    }

    /**
     * Check if transaction type can be refunded
     */
    public boolean isRefundable() {
        return this == TRANSFER_SENT || this == PAYMENT;
    }
}
