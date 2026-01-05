package com.mannapay.common.events.domain.payment;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mannapay.common.events.core.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


import java.math.BigDecimal;

/**
 * Base class for all payment-related domain events.
 *
 * Payment events capture state changes in the payment processing lifecycle:
 * - Payment initiation and authorization
 * - Capture and settlement
 * - Refunds and chargebacks
 * - Wallet transactions
 *
 * These events enable financial reconciliation and audit compliance.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "eventType",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PaymentInitiatedEvent.class, name = "PaymentInitiated"),
    @JsonSubTypes.Type(value = PaymentAuthorizedEvent.class, name = "PaymentAuthorized"),
    @JsonSubTypes.Type(value = PaymentCapturedEvent.class, name = "PaymentCaptured"),
    @JsonSubTypes.Type(value = PaymentCompletedEvent.class, name = "PaymentCompleted"),
    @JsonSubTypes.Type(value = PaymentFailedEvent.class, name = "PaymentFailed"),
    @JsonSubTypes.Type(value = PaymentRefundedEvent.class, name = "PaymentRefunded"),
    @JsonSubTypes.Type(value = WalletDebitedEvent.class, name = "WalletDebited"),
    @JsonSubTypes.Type(value = WalletCreditedEvent.class, name = "WalletCredited"),
    @JsonSubTypes.Type(value = WalletCreatedEvent.class, name = "WalletCreated"),
    @JsonSubTypes.Type(value = WalletFrozenEvent.class, name = "WalletFrozen"),
    @JsonSubTypes.Type(value = WalletUnfrozenEvent.class, name = "WalletUnfrozen")
})
public abstract class PaymentEvent extends DomainEvent {

    private static final long serialVersionUID = 1L;

    private String paymentId;
    private String walletId;
    private Long payerUserId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String paymentStatus;
    private String transactionReference;

    @Override
    public String getTopicName() {
        return "mannapay.payment.events";
    }

    @Override
    public String getPartitionKey() {
        // Partition by payer user ID for ordering within user's payments
        return payerUserId != null ? payerUserId.toString() : paymentId;
    }

    protected void initializePaymentEvent(String paymentId, Long payerUserId) {
        this.paymentId = paymentId;
        this.payerUserId = payerUserId;
        setAggregateId(paymentId);
        setAggregateType("Payment");
        initializeDefaults();
    }
}
