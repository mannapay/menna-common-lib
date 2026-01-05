package com.mannapay.common.events.domain.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event emitted when a transfer fails.
 *
 * This is a terminal event that triggers refund processing
 * and notification to the sender.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferFailedEvent extends TransferEvent {

    private static final long serialVersionUID = 1L;

    private String failureReason;
    private String failureCode;
    private String errorCode;
    private String failureStage; // COMPLIANCE, DEBIT, PROVIDER, SETTLEMENT
    private String failedStep; // Alias for failureStage
    private String providerErrorCode;
    private String providerErrorMessage;
    private boolean isRetryable;
    private int retryAttempt;
    private BigDecimal amountToRefund;
    private boolean refundRequired;
    private String recommendedAction;
    private Instant failedAt;

    public TransferFailedEvent(
            String transferId,
            Long senderId,
            String trackingNumber,
            String failureReason,
            String failureCode,
            String failureStage,
            boolean refundRequired) {

        initializeTransferEvent(transferId, senderId);
        setTrackingNumber(trackingNumber);
        setStatus("FAILED");
        setEventType("TransferFailed");

        this.failureReason = failureReason;
        this.failureCode = failureCode;
        this.failureStage = failureStage;
        this.refundRequired = refundRequired;
    }

    @Override
    public String getEventDescription() {
        return String.format("Transfer failed at %s: %s (%s)",
            failureStage, failureReason, failureCode);
    }
}
