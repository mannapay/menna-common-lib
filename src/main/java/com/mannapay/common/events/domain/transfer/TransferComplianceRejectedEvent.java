package com.mannapay.common.events.domain.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


import java.util.List;
import java.util.Map;

/**
 * Event emitted when compliance checks fail for a transfer.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferComplianceRejectedEvent extends TransferEvent {

    private static final long serialVersionUID = 1L;

    private String complianceCheckId;
    private String rejectionReason;
    private String rejectionCode;
    private List<String> failedChecks;
    private Map<String, String> checkDetails;
    private boolean isPermanentRejection;
    private String rejectedBy; // SYSTEM or reviewer ID
    private String recommendedAction;

    @Override
    public String getEventDescription() {
        return String.format("Compliance rejected for transfer %s: %s (%s)",
            getTransferId(), rejectionReason, rejectionCode);
    }
}
