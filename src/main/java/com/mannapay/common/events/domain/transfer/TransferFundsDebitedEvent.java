package com.mannapay.common.events.domain.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event emitted when funds are successfully debited from sender's wallet.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferFundsDebitedEvent extends TransferEvent {

    private static final long serialVersionUID = 1L;

    private String walletId;
    private BigDecimal debitedAmount;
    private String debitCurrency;
    private String currency; // Alias for debitCurrency
    private BigDecimal walletBalanceAfter;
    private BigDecimal newBalance; // Alias for walletBalanceAfter
    private String walletTransactionId;
    private String paymentReference;
    private Instant debitedAt;

    @Override
    public String getEventDescription() {
        return String.format("Funds debited: %s %s from wallet %s for transfer %s",
            debitedAmount, debitCurrency, walletId, getTransferId());
    }
}
