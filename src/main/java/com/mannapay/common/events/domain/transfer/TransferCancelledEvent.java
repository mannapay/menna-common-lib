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
 * Event emitted when a transfer is cancelled by the user or system.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferCancelledEvent extends TransferEvent {

    private static final long serialVersionUID = 1L;

    private String cancelledBy; // USER, SYSTEM, ADMIN
    private String cancellationReason;
    private String cancellationCode;
    private String statusAtCancellation;
    private BigDecimal amountToRefund;
    private boolean refundInitiated;
    private String refundTransactionId;
    private Instant cancelledAt;

    @Override
    public String getEventDescription() {
        return String.format("Transfer cancelled by %s: %s",
            cancelledBy, cancellationReason);
    }
}
