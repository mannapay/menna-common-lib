package com.mannapay.common.model.enums;

import lombok.Getter;

/**
 * Status values for money transfers throughout their lifecycle.
 */
@Getter
public enum TransferStatus {
    // Initial States
    INITIATED("Transfer initiated", "Transfer request has been created"),
    PENDING_PAYMENT("Pending payment", "Waiting for payment confirmation"),
    PAYMENT_RECEIVED("Payment received", "Payment has been confirmed"),

    // Validation States
    VALIDATING("Validating", "Validating transfer details"),
    COMPLIANCE_CHECK("Compliance check", "Undergoing compliance verification"),
    PENDING_APPROVAL("Pending approval", "Waiting for manual approval"),

    // Processing States
    PROCESSING("Processing", "Transfer is being processed"),
    SENT_TO_PROVIDER("Sent to provider", "Transfer sent to payment provider"),
    IN_TRANSIT("In transit", "Transfer is in transit"),

    // Recipient States
    READY_FOR_PICKUP("Ready for pickup", "Cash ready for recipient pickup"),
    AWAITING_RECIPIENT("Awaiting recipient", "Waiting for recipient to claim"),

    // Completion States
    COMPLETED("Completed", "Transfer completed successfully"),
    DELIVERED("Delivered", "Funds delivered to recipient"),

    // Failure States
    FAILED("Failed", "Transfer failed"),
    REJECTED("Rejected", "Transfer rejected by provider"),
    CANCELLED("Cancelled", "Transfer cancelled by user"),
    REFUNDED("Refunded", "Transfer amount refunded"),
    EXPIRED("Expired", "Transfer request expired"),

    // Special States
    ON_HOLD("On hold", "Transfer is on hold for review"),
    DISPUTED("Disputed", "Transfer is under dispute"),
    REVERSED("Reversed", "Transfer has been reversed");

    private final String displayName;
    private final String description;

    TransferStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Check if status represents a terminal state (no further processing)
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == DELIVERED || this == FAILED ||
               this == REJECTED || this == CANCELLED || this == REFUNDED || this == EXPIRED;
    }

    /**
     * Check if status represents a successful completion
     */
    public boolean isSuccess() {
        return this == COMPLETED || this == DELIVERED;
    }

    /**
     * Check if status represents a failure
     */
    public boolean isFailure() {
        return this == FAILED || this == REJECTED || this == CANCELLED ||
               this == REFUNDED || this == EXPIRED;
    }

    /**
     * Check if status represents an in-progress state
     */
    public boolean isInProgress() {
        return !isTerminal();
    }

    /**
     * Check if transfer can be cancelled in this status
     */
    public boolean isCancellable() {
        return this == INITIATED || this == PENDING_PAYMENT || this == VALIDATING ||
               this == PENDING_APPROVAL || this == ON_HOLD;
    }
}
