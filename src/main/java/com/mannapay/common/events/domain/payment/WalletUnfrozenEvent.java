package com.mannapay.common.events.domain.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


/**
 * Event emitted when a wallet is unfrozen.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WalletUnfrozenEvent extends PaymentEvent {

    private static final long serialVersionUID = 1L;

    private String unfreezeReason;
    private String unfrozenBy;
    private Long frozenDurationHours;

    @Override
    public String getTopicName() {
        return "mannapay.wallet.events";
    }

    @Override
    public String getEventDescription() {
        return String.format("Wallet unfrozen: %s by %s",
            getWalletId(), unfrozenBy);
    }
}
