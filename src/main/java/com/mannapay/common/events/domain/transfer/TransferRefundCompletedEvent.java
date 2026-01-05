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
 * Event emitted when a refund completes successfully.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferRefundCompletedEvent extends TransferEvent {

    private static final long serialVersionUID = 1L;

    private String refundId;
    private BigDecimal refundedAmount;
    private String refundCurrency;
    private String walletTransactionId;
    private BigDecimal walletBalanceAfter;
    private Instant refundedAt;

    @Override
    public String getEventDescription() {
        return String.format("Refund completed: %s %s credited to wallet",
            refundedAmount, refundCurrency);
    }
}
