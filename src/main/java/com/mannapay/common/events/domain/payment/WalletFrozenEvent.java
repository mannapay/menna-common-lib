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
 * Event emitted when a wallet is frozen.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WalletFrozenEvent extends PaymentEvent {

    private static final long serialVersionUID = 1L;

    private String freezeReason;
    private String freezeCode;
    private String frozenBy; // SYSTEM, COMPLIANCE, ADMIN
    private BigDecimal frozenBalance;
    private boolean allowDeposits;
    private boolean allowWithdrawals;

    @Override
    public String getTopicName() {
        return "mannapay.wallet.events";
    }

    @Override
    public String getEventDescription() {
        return String.format("Wallet frozen: %s by %s - %s",
            getWalletId(), frozenBy, freezeReason);
    }
}
