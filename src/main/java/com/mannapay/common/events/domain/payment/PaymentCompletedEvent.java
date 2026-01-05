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
 * Event emitted when a payment completes successfully.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentCompletedEvent extends PaymentEvent {

    private static final long serialVersionUID = 1L;

    private BigDecimal finalAmount;
    private BigDecimal processingFee;
    private BigDecimal netAmount;
    private Instant completedAt;
    private String gatewayConfirmation;
    private Long processingTimeMs;

    @Override
    public String getEventDescription() {
        return String.format("Payment completed: %s %s, net: %s",
            finalAmount, getCurrency(), netAmount);
    }
}
