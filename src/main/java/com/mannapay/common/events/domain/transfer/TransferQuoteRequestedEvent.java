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
 * Event emitted when a transfer quote is requested.
 *
 * Captures the quote request details for analytics and auditing.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferQuoteRequestedEvent extends TransferEvent {

    private static final long serialVersionUID = 1L;

    private Long quoteId;
    private BigDecimal requestedAmount;
    private String amountType; // SEND or RECEIVE
    private String deliveryMethod;
    private String recipientCountry;
    private BigDecimal exchangeRate;
    private BigDecimal feeAmount;
    private BigDecimal destinationAmount;
    private Instant quoteExpiresAt;

    @Override
    public String getEventDescription() {
        return String.format("Quote requested: %s %s to %s",
            requestedAmount, getSourceCurrency(), getDestinationCurrency());
    }
}
