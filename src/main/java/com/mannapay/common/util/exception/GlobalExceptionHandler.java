package com.mannapay.common.util.exception;

import com.mannapay.common.model.dto.ApiResponse;
import com.mannapay.common.model.dto.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Global exception handler for all REST controllers.
 * Provides consistent error responses across all services.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation exceptions from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        String traceId = UUID.randomUUID().toString();
        log.error("Validation error [{}]: {}", traceId, fieldErrors);

        ErrorDetails error = ErrorDetails.withFieldErrors(
                "VALIDATION_ERROR",
                "Validation failed",
                fieldErrors
        );
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setTraceId(traceId);

        ApiResponse<Void> response = ApiResponse.error("Validation failed", error);
        response.setStatus(HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle custom validation exceptions
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            ValidationException ex,
            WebRequest request) {

        String traceId = UUID.randomUUID().toString();
        log.error("Validation exception [{}]: {}", traceId, ex.getMessage());

        ErrorDetails error = ErrorDetails.withFieldErrors(
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getFieldErrors()
        );
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setTraceId(traceId);

        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), error);
        response.setStatus(HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle resource not found exceptions
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {

        String traceId = UUID.randomUUID().toString();
        log.error("Resource not found [{}]: {}", traceId, ex.getMessage());

        ErrorDetails error = ErrorDetails.of(ex.getErrorCode(), ex.getMessage());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setTraceId(traceId);

        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), error);
        response.setStatus(HttpStatus.NOT_FOUND.value());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle business exceptions
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex,
            WebRequest request) {

        String traceId = UUID.randomUUID().toString();
        log.error("Business exception [{}]: {}", traceId, ex.getMessage());

        ErrorDetails error = ErrorDetails.of(ex.getErrorCode(), ex.getMessage());
        error.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setTraceId(traceId);

        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), error);
        response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    /**
     * Handle unauthorized exceptions
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedException(
            UnauthorizedException ex,
            WebRequest request) {

        String traceId = UUID.randomUUID().toString();
        log.error("Unauthorized [{}]: {}", traceId, ex.getMessage());

        ErrorDetails error = ErrorDetails.of(ex.getErrorCode(), ex.getMessage());
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setTraceId(traceId);

        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), error);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handle forbidden exceptions
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbiddenException(
            ForbiddenException ex,
            WebRequest request) {

        String traceId = UUID.randomUUID().toString();
        log.error("Forbidden [{}]: {}", traceId, ex.getMessage());

        ErrorDetails error = ErrorDetails.of(ex.getErrorCode(), ex.getMessage());
        error.setStatus(HttpStatus.FORBIDDEN.value());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setTraceId(traceId);

        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), error);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(
            Exception ex,
            WebRequest request) {

        String traceId = UUID.randomUUID().toString();
        log.error("Internal server error [{}]", traceId, ex);

        ErrorDetails error = ErrorDetails.of(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please contact support."
        );
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setTraceId(traceId);
        error.setDetails(ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error("Internal server error", error);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
