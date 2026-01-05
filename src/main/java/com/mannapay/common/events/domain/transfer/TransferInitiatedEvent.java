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
 * Event emitted when a new money transfer is initiated.
 *
 * This is the first event in the transfer lifecycle and captures
 * all initial transfer details including amounts, currencies,
 * parties involved, and transfer type.
 *
 * Consumers:
 * - Compliance Service: Trigger AML/sanctions screening
 * - Fraud Detection: Real-time fraud scoring
 * - Notification Service: Send confirmation to sender
 * - Transaction Service: Create transaction record
 * - Audit Service: Log for regulatory compliance
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferInitiatedEvent extends TransferEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Amount in source currency.
     */
    private BigDecimal sourceAmount;

    /**
     * Amount to be received in destination currency.
     */
    private BigDecimal destinationAmount;

    /**
     * Total fee charged for the transfer.
     */
    private BigDecimal feeAmount;

    /**
     * Currency of the fee.
     */
    private String feeCurrency;

    /**
     * Exchange rate applied.
     */
    private BigDecimal exchangeRate;

    /**
     * Total amount charged (source + fee).
     */
    private BigDecimal totalChargedAmount;

    /**
     * Quote ID used for this transfer.
     */
    private Long quoteId;

    /**
     * Transfer type: WALLET_TO_BANK, WALLET_TO_WALLET, etc.
     */
    private String transferType;

    /**
     * Delivery method: BANK_TRANSFER, MOBILE_MONEY, CASH_PICKUP, etc.
     */
    private String deliveryMethod;

    /**
     * Purpose of transfer (for compliance).
     */
    private String purpose;

    /**
     * Recipient's name.
     */
    private String recipientName;

    /**
     * Recipient's country.
     */
    private String recipientCountry;

    /**
     * Recipient's account type.
     */
    private String recipientAccountType;

    /**
     * Payment method used by sender.
     */
    private String paymentMethod;

    /**
     * Sender's country.
     */
    private String senderCountry;

    /**
     * Source country (alias for senderCountry).
     */
    private String sourceCountry;

    /**
     * Destination country (alias for recipientCountry).
     */
    private String destinationCountry;

    /**
     * Sender's KYC level.
     */
    private String senderKycLevel;

    /**
     * IP address of the request.
     */
    private String ipAddress;

    /**
     * Device ID if from mobile app.
     */
    private String deviceId;

    /**
     * Expected delivery time.
     */
    private Instant estimatedDeliveryTime;

    public TransferInitiatedEvent(
            String transferId,
            Long senderId,
            Long recipientId,
            String trackingNumber,
            BigDecimal sourceAmount,
            String sourceCurrency,
            BigDecimal destinationAmount,
            String destinationCurrency,
            BigDecimal exchangeRate,
            BigDecimal feeAmount,
            String feeCurrency,
            String transferType,
            String purpose) {

        initializeTransferEvent(transferId, senderId);
        setRecipientId(recipientId);
        setTrackingNumber(trackingNumber);
        setSourceCurrency(sourceCurrency);
        setDestinationCurrency(destinationCurrency);
        setStatus("INITIATED");
        setEventType("TransferInitiated");

        this.sourceAmount = sourceAmount;
        this.destinationAmount = destinationAmount;
        this.exchangeRate = exchangeRate;
        this.feeAmount = feeAmount;
        this.feeCurrency = feeCurrency;
        this.transferType = transferType;
        this.purpose = purpose;
        this.totalChargedAmount = sourceAmount.add(feeAmount);
    }

    @Override
    public String getEventDescription() {
        return String.format("Transfer initiated: %s %s to %s, tracking: %s",
            sourceAmount, getSourceCurrency(), getDestinationCurrency(), getTrackingNumber());
    }
}
