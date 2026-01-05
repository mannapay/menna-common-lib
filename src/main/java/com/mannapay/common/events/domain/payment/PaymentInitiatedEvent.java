package com.mannapay.common.events.domain.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


import java.math.BigDecimal;

/**
 * Event emitted when a payment is initiated.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentInitiatedEvent extends PaymentEvent {

    private static final long serialVersionUID = 1L;

    private String transferId;
    private String cardToken;
    private String last4Digits;
    private String cardBrand;
    private String gatewaySessionId;
    private boolean requires3DS;
    private String ipAddress;

    @Override
    public String getEventDescription() {
        return String.format("Payment initiated: %s %s via %s",
            getAmount(), getCurrency(), getPaymentMethod());
    }
}
