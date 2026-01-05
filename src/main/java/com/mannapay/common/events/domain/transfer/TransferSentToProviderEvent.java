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
 * Event emitted when transfer is sent to Mastercard Send or other provider.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferSentToProviderEvent extends TransferEvent {

    private static final long serialVersionUID = 1L;

    private String providerName; // MASTERCARD_SEND, SWIFT, etc.
    private String providerTransactionId;
    private String providerStatus;
    private String providerReference;
    private BigDecimal providerFee;
    private Instant sentAt;
    private Instant expectedSettlementTime;
    private String paymentNetwork;

    @Override
    public String getEventDescription() {
        return String.format("Transfer %s sent to %s, provider txn: %s",
            getTransferId(), providerName, providerTransactionId);
    }
}
