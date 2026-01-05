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
 * Event emitted when a payment is captured.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentCapturedEvent extends PaymentEvent {

    private static final long serialVersionUID = 1L;

    private String captureId;
    private BigDecimal capturedAmount;
    private Instant capturedAt;
    private String settlementBatchId;

    @Override
    public String getEventDescription() {
        return String.format("Payment captured: %s %s",
            capturedAmount, getCurrency());
    }
}
