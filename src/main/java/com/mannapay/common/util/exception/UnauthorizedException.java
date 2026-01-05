package com.mannapay.common.util.exception;

/**
 * Exception thrown when user is not authenticated or token is invalid.
 */
public class UnauthorizedException extends CustomException {

    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super("UNAUTHORIZED", message, cause);
    }
}
