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
 * Event emitted when a dispute is opened for a transfer.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferDisputeOpenedEvent extends TransferEvent {

    private static final long serialVersionUID = 1L;

    private String disputeId;
    private String disputeType; // NOT_RECEIVED, WRONG_AMOUNT, UNAUTHORIZED, OTHER
    private String disputeReason;
    private BigDecimal disputedAmount;
    private String evidenceProvided;
    private String assignedTo;
    private String priority;

    @Override
    public String getEventDescription() {
        return String.format("Dispute opened for transfer %s: %s - %s",
            getTransferId(), disputeType, disputeReason);
    }
}
