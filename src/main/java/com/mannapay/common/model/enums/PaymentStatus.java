package com.mannapay.common.model.enums;

import lombok.Getter;

/**
 * Status values for payment transactions (MPGS payments).
 */
@Getter
public enum PaymentStatus {
    // Initial States
    INITIATED("Initiated", "Payment initiated"),
    SESSION_CREATED("Session created", "MPGS session created"),
    AWAITING_3DS("Awaiting 3DS", "Waiting for 3D Secure authentication"),

    // Authentication States
    AUTHENTICATED("Authenticated", "3DS authentication successful"),
    AUTHENTICATION_FAILED("Authentication failed", "3DS authentication failed"),

    // Authorization States
    AUTHORIZING("Authorizing", "Payment being authorized"),
    AUTHORIZED("Authorized", "Payment authorized"),
    AUTHORIZATION_FAILED("Authorization failed", "Authorization failed"),

    // Capture States
    CAPTURING("Capturing", "Payment being captured"),
    CAPTURED("Captured", "Payment captured successfully"),
    CAPTURE_FAILED("Capture failed", "Capture failed"),

    // Completion States
    COMPLETED("Completed", "Payment completed successfully"),
    SETTLED("Settled", "Payment settled"),

    // Reversal States
    VOIDING("Voiding", "Payment being voided"),
    VOIDED("Voided", "Payment voided"),
    VOID_FAILED("Void failed", "Void operation failed"),

    // Refund States
    REFUNDING("Refunding", "Refund in progress"),
    PARTIALLY_REFUNDED("Partially refunded", "Payment partially refunded"),
    FULLY_REFUNDED("Fully refunded", "Payment fully refunded"),
    REFUND_FAILED("Refund failed", "Refund operation failed"),

    // Failure States
    FAILED("Failed", "Payment failed"),
    DECLINED("Declined", "Payment declined by issuer"),
    EXPIRED("Expired", "Payment session expired"),
    CANCELLED("Cancelled", "Payment cancelled by user"),

    // Special States
    PENDING_REVIEW("Pending review", "Payment under review"),
    ON_HOLD("On hold", "Payment on hold"),
    DISPUTED("Disputed", "Payment disputed/chargeback initiated");

    private final String displayName;
    private final String description;

    PaymentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Check if status represents a terminal state
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == SETTLED || this == FAILED ||
               this == DECLINED || this == EXPIRED || this == CANCELLED ||
               this == VOIDED || this == FULLY_REFUNDED;
    }

    /**
     * Check if status represents a successful payment
     */
    public boolean isSuccess() {
        return this == COMPLETED || this == SETTLED || this == CAPTURED;
    }

    /**
     * Check if status represents a failure
     */
    public boolean isFailure() {
        return this == FAILED || this == DECLINED || this == EXPIRED ||
               this == AUTHENTICATION_FAILED || this == AUTHORIZATION_FAILED ||
               this == CAPTURE_FAILED;
    }

    /**
     * Check if payment is refundable in this status
     */
    public boolean isRefundable() {
        return this == CAPTURED || this == COMPLETED || this == SETTLED ||
               this == PARTIALLY_REFUNDED;
    }

    /**
     * Check if payment can be voided in this status
     */
    public boolean isVoidable() {
        return this == AUTHORIZED || this == CAPTURED;
    }
}
