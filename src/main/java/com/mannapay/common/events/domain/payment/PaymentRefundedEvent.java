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
 * Event emitted when a payment is refunded.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentRefundedEvent extends PaymentEvent {

    private static final long serialVersionUID = 1L;

    private String refundId;
    private BigDecimal refundAmount;
    private String refundReason;
    private boolean isPartialRefund;
    private BigDecimal originalAmount;
    private BigDecimal totalRefunded;
    private String gatewayRefundId;
    private Instant refundedAt;
    private String refundedBy;

    @Override
    public String getEventDescription() {
        return String.format("Payment refunded: %s %s, reason: %s",
            refundAmount, getCurrency(), refundReason);
    }
}
