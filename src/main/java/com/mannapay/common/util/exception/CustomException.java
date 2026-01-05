package com.mannapay.common.util.exception;

import lombok.Getter;

/**
 * Base custom exception class for all MannaPay exceptions.
 */
@Getter
public class CustomException extends RuntimeException {

    private final String errorCode;
    private final Object[] args;

    public CustomException(String message) {
        super(message);
        this.errorCode = "CUSTOM_ERROR";
        this.args = null;
    }

    public CustomException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = null;
    }

    public CustomException(String errorCode, String message, Object[] args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }

    public CustomException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = null;
    }

    public CustomException(String errorCode, String message, Object[] args, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = args;
    }
}
