package com.mannapay.common.util.validation;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Utility class for common validation operations.
 */
public final class ValidationUtil {

    private ValidationUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[1-9]\\d{1,14}$"); // E.164 format

    private static final Pattern ALPHANUMERIC_PATTERN =
            Pattern.compile("^[a-zA-Z0-9]+$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    /**
     * Validate email address
     */
    public static boolean isValidEmail(String email) {
        return StringUtils.isNotBlank(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate phone number (E.164 format)
     */
    public static boolean isValidPhoneNumber(String phone) {
        return StringUtils.isNotBlank(phone) && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validate alphanumeric string
     */
    public static boolean isAlphanumeric(String value) {
        return StringUtils.isNotBlank(value) && ALPHANUMERIC_PATTERN.matcher(value).matches();
    }

    /**
     * Validate password strength
     * Requirements: At least 8 characters, one uppercase, one lowercase, one digit, one special character
     */
    public static boolean isStrongPassword(String password) {
        return StringUtils.isNotBlank(password) && PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Validate string length
     */
    public static boolean isValidLength(String value, int minLength, int maxLength) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        int length = value.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Validate numeric string
     */
    public static boolean isNumeric(String value) {
        return StringUtils.isNumeric(value);
    }

    /**
     * Validate that value is not null or empty
     */
    public static boolean isNotEmpty(String value) {
        return StringUtils.isNotBlank(value);
    }

    /**
     * Validate URL format
     */
    public static boolean isValidUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        try {
            new java.net.URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate currency code (3 letters, uppercase)
     */
    public static boolean isValidCurrencyCode(String code) {
        return StringUtils.isNotBlank(code) &&
               code.length() == 3 &&
               code.matches("^[A-Z]{3}$");
    }

    /**
     * Validate amount (positive number with up to 2 decimal places)
     */
    public static boolean isValidAmount(String amount) {
        if (StringUtils.isBlank(amount)) {
            return false;
        }
        return amount.matches("^\\d+(\\.\\d{1,2})?$");
    }

    /**
     * Validate IBAN (basic format check)
     */
    public static boolean isValidIban(String iban) {
        if (StringUtils.isBlank(iban)) {
            return false;
        }
        // Remove spaces and convert to uppercase
        String cleanedIban = iban.replaceAll("\\s", "").toUpperCase();
        // Basic check: 2 letters followed by 2 digits, then alphanumeric
        return cleanedIban.matches("^[A-Z]{2}\\d{2}[A-Z0-9]+$") &&
               cleanedIban.length() >= 15 &&
               cleanedIban.length() <= 34;
    }

    /**
     * Validate SWIFT/BIC code
     */
    public static boolean isValidSwiftCode(String swift) {
        if (StringUtils.isBlank(swift)) {
            return false;
        }
        String cleanedSwift = swift.replaceAll("\\s", "").toUpperCase();
        // SWIFT: 8 or 11 characters
        return cleanedSwift.matches("^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$");
    }
}
