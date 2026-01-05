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
 * Event emitted when a payment is authorized by the gateway.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentAuthorizedEvent extends PaymentEvent {

    private static final long serialVersionUID = 1L;

    private String authorizationCode;
    private String gatewayTransactionId;
    private BigDecimal authorizedAmount;
    private Instant authorizedAt;
    private String avsResponse;
    private String cvvResponse;
    private String threeDSecureStatus;
    private boolean isPartialAuthorization;

    @Override
    public String getEventDescription() {
        return String.format("Payment authorized: %s %s, auth code: %s",
            authorizedAmount, getCurrency(), authorizationCode);
    }
}
