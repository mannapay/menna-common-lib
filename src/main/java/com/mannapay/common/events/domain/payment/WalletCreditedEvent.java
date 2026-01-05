package com.mannapay.common.events.domain.payment;

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
 * Event emitted when funds are credited to a wallet.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WalletCreditedEvent extends PaymentEvent {

    private static final long serialVersionUID = 1L;

    private BigDecimal creditAmount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String walletTransactionId;
    private String creditReason;
    private String referenceId;
    private String referenceType; // DEPOSIT, REFUND, TRANSFER_IN, REVERSAL
    private String sourceDescription;
    private Instant creditedAt;

    public WalletCreditedEvent(
            String walletId,
            Long userId,
            BigDecimal creditAmount,
            String currency,
            BigDecimal balanceAfter,
            String referenceId,
            String referenceType) {

        initializePaymentEvent(walletId, userId);
        setWalletId(walletId);
        setAmount(creditAmount);
        setCurrency(currency);
        setEventType("WalletCredited");
        setPaymentStatus("COMPLETED");

        this.creditAmount = creditAmount;
        this.balanceAfter = balanceAfter;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
        this.creditedAt = Instant.now();
    }

    @Override
    public String getTopicName() {
        return "mannapay.wallet.events";
    }

    @Override
    public String getEventDescription() {
        return String.format("Wallet credited: %s %s from %s %s",
            creditAmount, getCurrency(), referenceType, referenceId);
    }
}
