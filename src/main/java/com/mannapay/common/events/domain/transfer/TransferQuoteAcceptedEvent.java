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
 * Event emitted when a transfer quote is accepted by the user.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferQuoteAcceptedEvent extends TransferEvent {

    private static final long serialVersionUID = 1L;

    private Long quoteId;
    private BigDecimal lockedExchangeRate;
    private BigDecimal lockedFeeAmount;
    private String paymentMethodSelected;

    @Override
    public String getEventDescription() {
        return String.format("Quote %d accepted with rate %s", quoteId, lockedExchangeRate);
    }
}
