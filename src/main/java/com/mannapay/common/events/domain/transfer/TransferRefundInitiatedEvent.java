package com.mannapay.common.events.domain.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


import java.math.BigDecimal;

/**
 * Event emitted when a refund is initiated for a failed/cancelled transfer.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferRefundInitiatedEvent extends TransferEvent {

    private static final long serialVersionUID = 1L;

    private String refundId;
    private BigDecimal refundAmount;
    private String refundCurrency;
    private String currency; // Alias for refundCurrency
    private String refundReason;
    private String refundMethod; // WALLET, ORIGINAL_PAYMENT_METHOD
    private String originalPaymentMethod;
    private String originalTransactionReference;
    private boolean isPartialRefund;
    private BigDecimal originalAmount;

    @Override
    public String getEventDescription() {
        return String.format("Refund initiated: %s %s for transfer %s",
            refundAmount, refundCurrency, getTransferId());
    }
}
