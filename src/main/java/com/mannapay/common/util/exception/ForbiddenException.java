package com.mannapay.common.util.exception;

/**
 * Exception thrown when user does not have permission to perform an action.
 */
public class ForbiddenException extends CustomException {

    public ForbiddenException(String message) {
        super("FORBIDDEN", message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super("FORBIDDEN", message, cause);
    }
}
