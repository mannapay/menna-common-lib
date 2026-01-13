package com.mannapay.common.events.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Event published on user login attempts (success or failure).
 *
 * This is a high-volume audit event used for:
 * - Security monitoring and fraud detection
 * - Login pattern analysis
 * - Compliance audit trail
 * - Account lockout tracking
 *
 * Published by: auth-service
 * Consumed by: fraud-detection-service, audit-service
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
public class UserLoginEvent extends UserEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Whether login was successful.
     */
    private boolean success;

    /**
     * Failure reason if login failed.
     * Values: INVALID_CREDENTIALS, ACCOUNT_LOCKED, ACCOUNT_DISABLED,
     *         2FA_REQUIRED, 2FA_FAILED, SESSION_EXPIRED
     */
    private String failureReason;

    /**
     * Login method used.
     * Values: PASSWORD, SOCIAL_GOOGLE, SOCIAL_APPLE, BIOMETRIC, SSO
     */
    private String loginMethod;

    /**
     * Whether two-factor authentication was required.
     */
    private boolean twoFactorRequired;

    /**
     * Whether two-factor authentication was completed successfully.
     */
    private boolean twoFactorCompleted;

    /**
     * Two-factor method used (if applicable).
     * Values: TOTP, SMS, EMAIL
     */
    private String twoFactorMethod;

    /**
     * Geographic location derived from IP (if available).
     */
    private String geoLocation;

    /**
     * Country code from geo-location.
     */
    private String countryCode;

    /**
     * Whether this login is from a new device.
     */
    private boolean newDevice;

    /**
     * Whether this login is from a trusted device.
     */
    private boolean trustedDevice;

    /**
     * Session ID created (if login successful).
     */
    private String sessionId;

    /**
     * Number of failed login attempts before this one (if failed).
     */
    private int failedAttemptCount;

    /**
     * Whether account was locked as a result of this attempt.
     */
    private boolean accountLockedAfter;

    /**
     * Constructor for successful login.
     */
    public static UserLoginEvent success(String keycloakId, String email,
                                          String ipAddress, String userAgent,
                                          String deviceId, String sessionId) {
        UserLoginEvent event = new UserLoginEvent();
        event.initializeUserEvent(keycloakId, email);
        event.setEventType("UserLogin");
        event.setIpAddress(ipAddress);
        event.setUserAgent(userAgent);
        event.setDeviceId(deviceId);
        event.success = true;
        event.sessionId = sessionId;
        event.loginMethod = "PASSWORD";
        return event;
    }

    /**
     * Constructor for failed login.
     */
    public static UserLoginEvent failure(String keycloakId, String email,
                                          String ipAddress, String userAgent,
                                          String failureReason, int failedAttemptCount) {
        UserLoginEvent event = new UserLoginEvent();
        event.initializeUserEvent(keycloakId, email);
        event.setEventType("UserLogin");
        event.setIpAddress(ipAddress);
        event.setUserAgent(userAgent);
        event.success = false;
        event.failureReason = failureReason;
        event.failedAttemptCount = failedAttemptCount;
        event.loginMethod = "PASSWORD";
        return event;
    }

    @Override
    public String getTopicName() {
        return UserEventTopics.USER_LOGIN_EVENTS;
    }

    @Override
    public String getEventDescription() {
        if (success) {
            return String.format("User login successful: %s from %s, 2FA: %s",
                getEmail(), getIpAddress(), twoFactorCompleted ? "completed" : "not required");
        } else {
            return String.format("User login failed: %s from %s, reason: %s, attempt #%d",
                getEmail(), getIpAddress(), failureReason, failedAttemptCount);
        }
    }
}
