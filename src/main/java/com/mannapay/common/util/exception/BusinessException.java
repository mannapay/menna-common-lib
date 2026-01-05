package com.mannapay.common.util.exception;

/**
 * Exception thrown when business rules are violated.
 */
public class BusinessException extends CustomException {

    public BusinessException(String message) {
        super("BUSINESS_ERROR", message);
    }

    public BusinessException(String errorCode, String message) {
        super(errorCode, message);
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
