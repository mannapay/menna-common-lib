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
 * Event emitted when a dispute is resolved.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferDisputeResolvedEvent extends TransferEvent {

    private static final long serialVersionUID = 1L;

    private String disputeId;
    private String resolution; // REFUNDED, REJECTED, PARTIALLY_REFUNDED
    private String resolutionReason;
    private String resolvedBy;
    private Instant resolvedAt;
    private BigDecimal refundAmount;
    private boolean customerNotified;
    private Long resolutionTimeHours;

    @Override
    public String getEventDescription() {
        return String.format("Dispute %s resolved: %s - %s",
            disputeId, resolution, resolutionReason);
    }
}
