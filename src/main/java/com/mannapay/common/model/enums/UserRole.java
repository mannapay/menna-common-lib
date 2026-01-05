package com.mannapay.common.model.enums;

import lombok.Getter;

/**
 * User roles for access control.
 */
@Getter
public enum UserRole {
    ROLE_USER("User", "Regular platform user"),
    ROLE_ADMIN("Admin", "Platform administrator"),
    ROLE_SUPPORT("Support", "Customer support agent"),
    ROLE_COMPLIANCE("Compliance", "Compliance officer"),
    ROLE_FINANCE("Finance", "Finance team member"),
    ROLE_AGENT("Agent", "Partner agent for cash pickup/delivery");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Check if role has administrative privileges
     */
    public boolean isAdmin() {
        return this == ROLE_ADMIN;
    }

    /**
     * Check if role can access user data
     */
    public boolean canAccessUserData() {
        return this == ROLE_ADMIN || this == ROLE_SUPPORT || this == ROLE_COMPLIANCE;
    }

    /**
     * Check if role can process transactions
     */
    public boolean canProcessTransactions() {
        return this == ROLE_ADMIN || this == ROLE_FINANCE || this == ROLE_AGENT;
    }
}
