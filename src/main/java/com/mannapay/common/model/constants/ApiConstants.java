package com.mannapay.common.model.constants;

/**
 * API-related constants used across all services.
 */
public final class ApiConstants {

    private ApiConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // API Versioning
    public static final String API_VERSION = "v1";
    public static final String API_BASE_PATH = "/api/" + API_VERSION;

    // Service Endpoints
    public static final String AUTH_SERVICE = "auth-service";
    public static final String USER_SERVICE = "user-service";
    public static final String PAYMENT_SERVICE = "payment-service";
    public static final String TRANSFER_SERVICE = "transfer-service";
    public static final String RECIPIENT_SERVICE = "recipient-service";
    public static final String TRANSACTION_SERVICE = "transaction-service";
    public static final String EXCHANGE_RATE_SERVICE = "exchange-rate-service";
    public static final String NOTIFICATION_SERVICE = "notification-service";
    public static final String COMPLIANCE_SERVICE = "compliance-service";

    // Common Headers
    public static final String HEADER_AUTH = "Authorization";
    public static final String HEADER_BEARER_PREFIX = "Bearer ";
    public static final String HEADER_API_KEY = "X-API-Key";
    public static final String HEADER_REQUEST_ID = "X-Request-ID";
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";
    public static final String HEADER_CLIENT_VERSION = "X-Client-Version";
    public static final String HEADER_DEVICE_ID = "X-Device-ID";
    public static final String HEADER_PLATFORM = "X-Platform";

    // Pagination Defaults
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_DIRECTION = "DESC";
    public static final String DEFAULT_SORT_FIELD = "createdAt";

    // Request Parameters
    public static final String PARAM_PAGE = "page";
    public static final String PARAM_SIZE = "size";
    public static final String PARAM_SORT = "sort";
    public static final String PARAM_SEARCH = "search";
    public static final String PARAM_FILTER = "filter";

    // Error Codes
    public static final String ERROR_VALIDATION = "VALIDATION_ERROR";
    public static final String ERROR_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String ERROR_UNAUTHORIZED = "UNAUTHORIZED";
    public static final String ERROR_FORBIDDEN = "FORBIDDEN";
    public static final String ERROR_CONFLICT = "CONFLICT";
    public static final String ERROR_INTERNAL = "INTERNAL_SERVER_ERROR";
    public static final String ERROR_SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";
    public static final String ERROR_BAD_REQUEST = "BAD_REQUEST";

    // Cache Keys
    public static final String CACHE_EXCHANGE_RATES = "exchange-rates";
    public static final String CACHE_USER_PROFILES = "user-profiles";
    public static final String CACHE_RECIPIENTS = "recipients";
    public static final String CACHE_TRANSFER_QUOTES = "transfer-quotes";

    // Cache TTL (in seconds)
    public static final long CACHE_TTL_SHORT = 300;      // 5 minutes
    public static final long CACHE_TTL_MEDIUM = 1800;    // 30 minutes
    public static final long CACHE_TTL_LONG = 3600;      // 1 hour
    public static final long CACHE_TTL_EXCHANGE_RATE = 3600; // 1 hour

    // Rate Limiting
    public static final int RATE_LIMIT_PER_MINUTE = 60;
    public static final int RATE_LIMIT_PER_HOUR = 1000;

    // File Upload
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    public static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/jpg"};
    public static final String[] ALLOWED_DOCUMENT_TYPES = {"application/pdf", "image/jpeg", "image/png"};

    // Date/Time Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATETIME_FORMAT_WITH_ZONE = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    // Regex Patterns
    public static final String REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String REGEX_PHONE = "^\\+?[1-9]\\d{1,14}$"; // E.164 format
    public static final String REGEX_ALPHANUMERIC = "^[a-zA-Z0-9]+$";
    public static final String REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
}
