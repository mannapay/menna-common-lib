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
 * Event emitted when a transfer completes successfully.
 *
 * This is a terminal event indicating the funds have been
 * delivered to the recipient.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferCompletedEvent extends TransferEvent {

    private static final long serialVersionUID = 1L;

    private BigDecimal finalSourceAmount;
    private BigDecimal finalDestinationAmount;
    private BigDecimal finalAmount; // Alias for finalDestinationAmount
    private String finalCurrency; // Alias for destinationCurrency
    private BigDecimal finalFeeAmount;
    private BigDecimal finalExchangeRate;
    private String providerConfirmationNumber;
    private String confirmationNumber; // Alias for providerConfirmationNumber
    private String providerReference;
    private String recipientReference;
    private Instant deliveredAt;
    private Instant completedAt;
    private Long processingTimeMs;
    private String settlementBatch;

    public TransferCompletedEvent(
            String transferId,
            Long senderId,
            Long recipientId,
            String trackingNumber,
            BigDecimal finalDestinationAmount,
            String destinationCurrency,
            String providerConfirmationNumber) {

        initializeTransferEvent(transferId, senderId);
        setRecipientId(recipientId);
        setTrackingNumber(trackingNumber);
        setDestinationCurrency(destinationCurrency);
        setStatus("COMPLETED");
        setEventType("TransferCompleted");

        this.finalDestinationAmount = finalDestinationAmount;
        this.providerConfirmationNumber = providerConfirmationNumber;
        this.deliveredAt = Instant.now();
    }

    @Override
    public String getEventDescription() {
        return String.format("Transfer completed: %s %s delivered, confirmation: %s",
            finalDestinationAmount, getDestinationCurrency(), providerConfirmationNumber);
    }
}
