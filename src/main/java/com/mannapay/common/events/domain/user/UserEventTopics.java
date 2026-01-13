package com.mannapay.common.events.domain.user;

/**
 * Kafka topic names for user domain events.
 *
 * Topic naming convention: mannapay.{domain}.{event-type}
 *
 * @author MannaPay Architecture Team
 * @version 1.0.0
 */
public final class UserEventTopics {

    private UserEventTopics() {
        // Utility class - prevent instantiation
    }

    /**
     * Main topic for user lifecycle events.
     * Events: UserRegistered, UserProfileCreated, UserProfileUpdated,
     *         UserDeactivated, UserReactivated
     *
     * Publishers: auth-service, user-service
     * Consumers: user-service, notification-service
     */
    public static final String USER_EVENTS = "mannapay.user.events";

    /**
     * Topic for user authentication/security events.
     * High-volume audit events separated for performance.
     * Events: UserLogin (success/failure), UserPasswordChanged
     *
     * Publishers: auth-service
     * Consumers: fraud-detection-service, audit-service
     */
    public static final String USER_LOGIN_EVENTS = "mannapay.user.login-events";

    /**
     * Topic for KYC/compliance related events.
     * Events: UserKYCStatusChanged, UserEmailVerified
     *
     * Publishers: user-service, compliance-service
     * Consumers: transfer-service, payment-service, compliance-service
     */
    public static final String USER_KYC_EVENTS = "mannapay.user.kyc-events";

    /**
     * Dead letter queue for failed user events.
     */
    public static final String USER_EVENTS_DLQ = "mannapay.user.events.dlq";
}
