package com.mannapay.common.events.domain.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


import java.util.Map;

/**
 * Event emitted when compliance checks pass for a transfer.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferComplianceApprovedEvent extends TransferEvent {

    private static final long serialVersionUID = 1L;

    private String complianceCheckId;
    private String overallRiskScore;
    private Map<String, String> checkResults; // Check type -> result
    private String approvedBy; // SYSTEM or reviewer ID
    private boolean requiresManualReview;

    @Override
    public String getEventDescription() {
        return String.format("Compliance approved for transfer %s, risk: %s",
            getTransferId(), overallRiskScore);
    }
}
