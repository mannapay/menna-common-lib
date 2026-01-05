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
 * Event emitted when a new wallet is created.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WalletCreatedEvent extends PaymentEvent {

    private static final long serialVersionUID = 1L;

    private String walletType; // PERSONAL, BUSINESS
    private BigDecimal initialBalance;
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;

    @Override
    public String getTopicName() {
        return "mannapay.wallet.events";
    }

    @Override
    public String getEventDescription() {
        return String.format("Wallet created: %s %s wallet for user %d",
            getCurrency(), walletType, getPayerUserId());
    }
}
