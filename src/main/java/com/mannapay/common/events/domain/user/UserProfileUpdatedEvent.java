package com.mannapay.common.events.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * Event published when user profile is updated.
 *
 * This event captures profile changes for audit trail and
 * notification purposes.
 *
 * Published by: user-service
 * Consumed by: notification-service, audit-service
 *
 * @author MannaPay Architecture Team
 * @version 1.0.0
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserProfileUpdatedEvent extends UserEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Profile ID in user-service database.
     */
    private Long profileId;

    /**
     * Map of changed fields with their new values.
     * Key: field name, Value: new value (as string)
     */
    private Map<String, String> changedFields;

    /**
     * Map of previous values for changed fields (for audit).
     * Key: field name, Value: previous value (as string)
     */
    private Map<String, String> previousValues;

    /**
     * Who made the update: USER, ADMIN, SYSTEM
     */
    private String updatedBy;

    /**
     * Reason for update (optional, for admin updates).
     */
    private String updateReason;

    /**
     * Whether the update was made via admin portal.
     */
    private boolean adminUpdate;

    @Override
    public String getEventDescription() {
        int fieldCount = changedFields != null ? changedFields.size() : 0;
        return String.format("User profile updated: profileId=%d, %d field(s) changed by %s",
            profileId, fieldCount, updatedBy);
    }
}
