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
import java.util.List;

/**
 * Event emitted when compliance checks begin for a transfer.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferComplianceCheckStartedEvent extends TransferEvent {

    private static final long serialVersionUID = 1L;

    private BigDecimal amount;
    private List<String> checksToPerform; // SANCTIONS, AML, PEP, VELOCITY
    private List<String> checkTypes; // Alias for checksToPerform
    private Instant startedAt;
    private String senderRiskLevel;
    private String recipientRiskLevel;
    private String corridorRiskLevel;

    @Override
    public String getEventDescription() {
        return String.format("Compliance check started for transfer %s: %s",
            getTransferId(), String.join(", ", checksToPerform));
    }
}
