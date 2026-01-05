package com.mannapay.common.events.domain.transfer;

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
 * Base class for all transfer-related domain events.
 *
 * Transfer events capture all state changes in the money transfer lifecycle:
 * - Quote generation and acceptance
 * - Transfer initiation and processing
 * - Compliance checks and approvals
 * - Completion, failure, and cancellation
 * - Refunds and disputes
 *
 * These events form the audit trail required for financial compliance
 * and enable event sourcing for transfer aggregates.
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
    @JsonSubTypes.Type(value = TransferInitiatedEvent.class, name = "TransferInitiated"),
    @JsonSubTypes.Type(value = TransferQuoteRequestedEvent.class, name = "TransferQuoteRequested"),
    @JsonSubTypes.Type(value = TransferQuoteAcceptedEvent.class, name = "TransferQuoteAccepted"),
    @JsonSubTypes.Type(value = TransferComplianceCheckStartedEvent.class, name = "TransferComplianceCheckStarted"),
    @JsonSubTypes.Type(value = TransferComplianceApprovedEvent.class, name = "TransferComplianceApproved"),
    @JsonSubTypes.Type(value = TransferComplianceRejectedEvent.class, name = "TransferComplianceRejected"),
    @JsonSubTypes.Type(value = TransferFundsDebitedEvent.class, name = "TransferFundsDebited"),
    @JsonSubTypes.Type(value = TransferSentToProviderEvent.class, name = "TransferSentToProvider"),
    @JsonSubTypes.Type(value = TransferProcessingEvent.class, name = "TransferProcessing"),
    @JsonSubTypes.Type(value = TransferCompletedEvent.class, name = "TransferCompleted"),
    @JsonSubTypes.Type(value = TransferFailedEvent.class, name = "TransferFailed"),
    @JsonSubTypes.Type(value = TransferCancelledEvent.class, name = "TransferCancelled"),
    @JsonSubTypes.Type(value = TransferRefundInitiatedEvent.class, name = "TransferRefundInitiated"),
    @JsonSubTypes.Type(value = TransferRefundCompletedEvent.class, name = "TransferRefundCompleted"),
    @JsonSubTypes.Type(value = TransferDisputeOpenedEvent.class, name = "TransferDisputeOpened"),
    @JsonSubTypes.Type(value = TransferDisputeResolvedEvent.class, name = "TransferDisputeResolved")
})
public abstract class TransferEvent extends DomainEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Transfer ID - the aggregate root identifier.
     */
    private String transferId;

    /**
     * User who initiated the transfer.
     */
    private Long senderId;

    /**
     * Recipient of the transfer.
     */
    private Long recipientId;

    /**
     * Transfer tracking number for external reference.
     */
    private String trackingNumber;

    /**
     * Source currency code (ISO 4217).
     */
    private String sourceCurrency;

    /**
     * Destination currency code (ISO 4217).
     */
    private String destinationCurrency;

    /**
     * Current transfer status after this event.
     */
    private String status;

    @Override
    public String getTopicName() {
        return "mannapay.transfer.events";
    }

    @Override
    public String getPartitionKey() {
        // Partition by transfer ID for ordering within a transfer
        return transferId != null ? transferId : super.getPartitionKey();
    }

    /**
     * Initialize with aggregate information.
     */
    protected void initializeTransferEvent(String transferId, Long senderId) {
        this.transferId = transferId;
        this.senderId = senderId;
        setAggregateId(transferId);
        setAggregateType("Transfer");
        initializeDefaults();
    }
}
