package com.mannapay.common.model.constants;

/**
 * Validation-related constants for data validation across services.
 */
public final class ValidationConstants {

    private ValidationConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Field Length Constraints
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final int MAX_PHONE_LENGTH = 20;
    public static final int MAX_ADDRESS_LINE_LENGTH = 255;
    public static final int MAX_CITY_LENGTH = 100;
    public static final int MAX_POSTAL_CODE_LENGTH = 20;
    public static final int MAX_COUNTRY_CODE_LENGTH = 3;
    public static final int MAX_DESCRIPTION_LENGTH = 500;
    public static final int MAX_REFERENCE_LENGTH = 50;

    // Password Constraints
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 128;

    // Amount Constraints
    public static final String MIN_TRANSFER_AMOUNT = "1.00";
    public static final String MAX_TRANSFER_AMOUNT = "50000.00";
    public static final String MIN_EXCHANGE_RATE = "0.0001";
    public static final String MAX_EXCHANGE_RATE = "10000.0";

    // Validation Messages
    public static final String MSG_REQUIRED = "This field is required";
    public static final String MSG_INVALID_EMAIL = "Invalid email format";
    public static final String MSG_INVALID_PHONE = "Invalid phone number format";
    public static final String MSG_INVALID_AMOUNT = "Invalid amount";
    public static final String MSG_INVALID_CURRENCY = "Invalid currency code";
    public static final String MSG_INVALID_PASSWORD = "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character";
    public static final String MSG_PASSWORD_MISMATCH = "Passwords do not match";
    public static final String MSG_INVALID_DATE = "Invalid date format";
    public static final String MSG_FUTURE_DATE = "Date cannot be in the future";
    public static final String MSG_PAST_DATE = "Date cannot be in the past";

    // Entity-specific Messages
    public static final String MSG_USER_NOT_FOUND = "User not found";
    public static final String MSG_TRANSFER_NOT_FOUND = "Transfer not found";
    public static final String MSG_PAYMENT_NOT_FOUND = "Payment not found";
    public static final String MSG_RECIPIENT_NOT_FOUND = "Recipient not found";
    public static final String MSG_TRANSACTION_NOT_FOUND = "Transaction not found";

    // Business Rule Messages
    public static final String MSG_INSUFFICIENT_BALANCE = "Insufficient balance";
    public static final String MSG_TRANSFER_LIMIT_EXCEEDED = "Transfer limit exceeded for your KYC level";
    public static final String MSG_DUPLICATE_RECIPIENT = "Recipient already exists";
    public static final String MSG_INVALID_TRANSFER_STATUS = "Cannot perform this operation in current transfer status";
    public static final String MSG_SAME_CURRENCY_TRANSFER = "Sender and recipient currencies are the same";
    public static final String MSG_UNSUPPORTED_CURRENCY = "Currency not supported";
    public static final String MSG_KYC_REQUIRED = "KYC verification required for this transaction";

    // File Upload Messages
    public static final String MSG_FILE_TOO_LARGE = "File size exceeds maximum allowed size";
    public static final String MSG_INVALID_FILE_TYPE = "Invalid file type";
    public static final String MSG_FILE_UPLOAD_FAILED = "File upload failed";

    // Authentication Messages
    public static final String MSG_INVALID_CREDENTIALS = "Invalid email or password";
    public static final String MSG_ACCOUNT_LOCKED = "Account is locked";
    public static final String MSG_ACCOUNT_DISABLED = "Account is disabled";
    public static final String MSG_TOKEN_EXPIRED = "Token has expired";
    public static final String MSG_INVALID_TOKEN = "Invalid token";
    public static final String MSG_2FA_REQUIRED = "Two-factor authentication required";
    public static final String MSG_INVALID_2FA_CODE = "Invalid 2FA code";
}
