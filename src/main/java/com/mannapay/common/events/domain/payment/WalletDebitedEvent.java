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
 * Event emitted when funds are debited from a wallet.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WalletDebitedEvent extends PaymentEvent {

    private static final long serialVersionUID = 1L;

    private BigDecimal debitAmount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String walletTransactionId;
    private String debitReason;
    private String referenceId;
    private String referenceType; // TRANSFER, FEE, WITHDRAWAL
    private Instant debitedAt;
    private boolean isHold; // Temporary hold vs permanent debit

    public WalletDebitedEvent(
            String walletId,
            Long userId,
            BigDecimal debitAmount,
            String currency,
            BigDecimal balanceAfter,
            String referenceId,
            String referenceType) {

        initializePaymentEvent(walletId, userId);
        setWalletId(walletId);
        setAmount(debitAmount);
        setCurrency(currency);
        setEventType("WalletDebited");
        setPaymentStatus("COMPLETED");

        this.debitAmount = debitAmount;
        this.balanceAfter = balanceAfter;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
        this.debitedAt = Instant.now();
    }

    @Override
    public String getTopicName() {
        return "mannapay.wallet.events";
    }

    @Override
    public String getEventDescription() {
        return String.format("Wallet debited: %s %s for %s %s",
            debitAmount, getCurrency(), referenceType, referenceId);
    }
}
