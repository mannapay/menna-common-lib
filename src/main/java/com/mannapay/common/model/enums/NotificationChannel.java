package com.mannapay.common.model.enums;

import lombok.Getter;

/**
 * Available notification channels for user communications.
 */
@Getter
public enum NotificationChannel {
    EMAIL("Email", "Email notification", true),
    SMS("SMS", "SMS text message", true),
    PUSH("Push notification", "Mobile push notification", true),
    IN_APP("In-app", "In-app notification", false),
    WEBHOOK("Webhook", "Webhook callback", false);

    private final String displayName;
    private final String description;
    private final boolean userSelectable;

    NotificationChannel(String displayName, String description, boolean userSelectable) {
        this.displayName = displayName;
        this.description = description;
        this.userSelectable = userSelectable;
    }

    /**
     * Check if channel requires user contact information
     */
    public boolean requiresContactInfo() {
        return this == EMAIL || this == SMS;
    }

    /**
     * Check if channel supports rich content (HTML, images, etc.)
     */
    public boolean supportsRichContent() {
        return this == EMAIL || this == PUSH || this == IN_APP;
    }
}
