package com.mannapay.common.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Error details structure for API error responses.
 * Provides detailed error information for debugging and user feedback.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Detailed error information")
public class ErrorDetails {

    @Schema(description = "Error code", example = "VALIDATION_ERROR")
    private String code;

    @Schema(description = "Error message", example = "Validation failed")
    private String message;

    @Schema(description = "Detailed error description")
    private String details;

    @Schema(description = "Field-level validation errors")
    private Map<String, String> fieldErrors;

    @Schema(description = "List of error messages")
    private List<String> errors;

    @Schema(description = "Error timestamp")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "Request path where error occurred", example = "/api/v1/users")
    private String path;

    @Schema(description = "HTTP status code", example = "400")
    private Integer status;

    @Schema(description = "Error trace ID for debugging", example = "abc-123-def-456")
    private String traceId;

    /**
     * Create error details with code and message
     */
    public static ErrorDetails of(String code, String message) {
        return ErrorDetails.builder()
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error details with field errors
     */
    public static ErrorDetails withFieldErrors(String code, String message, Map<String, String> fieldErrors) {
        return ErrorDetails.builder()
                .code(code)
                .message(message)
                .fieldErrors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error details with error list
     */
    public static ErrorDetails withErrors(String code, String message, List<String> errors) {
        return ErrorDetails.builder()
                .code(code)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
