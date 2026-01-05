package com.mannapay.common.events.domain.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


import java.time.Instant;

/**
 * Event emitted when a payment fails.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentFailedEvent extends PaymentEvent {

    private static final long serialVersionUID = 1L;

    private String failureReason;
    private String failureCode;
    private String gatewayErrorCode;
    private String gatewayErrorMessage;
    private String failureStage;
    private boolean isRetryable;
    private int attemptNumber;
    private Instant failedAt;

    @Override
    public String getEventDescription() {
        return String.format("Payment failed: %s (%s)",
            failureReason, failureCode);
    }
}
