package com.mannapay.common.events.domain.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Event emitted when transfer status updates during processing.
 */
@Getter
@Setter
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransferProcessingEvent extends TransferEvent {

    private static final long serialVersionUID = 1L;

    private String previousStatus;
    private String newStatus;
    private String processingStage;
    private String step; // Alias for processingStage
    private String providerName;
    private String providerReference;
    private String providerStatus;
    private String statusMessage;
    private int progressPercentage;
    private Instant estimatedCompletionTime;

    @Override
    public String getEventDescription() {
        return String.format("Transfer %s processing: %s -> %s (%s)",
            getTransferId(), previousStatus, newStatus, processingStage);
    }
}
