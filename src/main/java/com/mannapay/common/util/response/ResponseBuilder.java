package com.mannapay.common.util.response;

import com.mannapay.common.model.dto.ApiResponse;
import com.mannapay.common.model.dto.ErrorDetails;
import com.mannapay.common.model.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Utility class for building consistent API responses.
 */
public final class ResponseBuilder {

    private ResponseBuilder() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Build success response with data
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * Build success response with message and data
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }

    /**
     * Build success response with custom status
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, HttpStatus status) {
        ApiResponse<T> response = ApiResponse.success(data);
        response.setStatus(status.value());
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Build created response (HTTP 201)
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        ApiResponse<T> response = ApiResponse.success("Resource created successfully", data);
        response.setStatus(HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Build created response with message
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        ApiResponse<T> response = ApiResponse.success(message, data);
        response.setStatus(HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Build success response with only message (no data)
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message) {
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    /**
     * Build no content response (HTTP 204)
     */
    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    /**
     * Build paginated success response
     */
    public static <T> ResponseEntity<ApiResponse<PageResponse<T>>> successPage(Page<T> page) {
        PageResponse<T> pageResponse = PageResponse.of(page);
        ApiResponse<PageResponse<T>> response = ApiResponse.success(pageResponse);
        return ResponseEntity.ok(response);
    }

    /**
     * Build error response
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status) {
        ErrorDetails error = ErrorDetails.of(status.name(), message);
        error.setStatus(status.value());

        ApiResponse<T> response = ApiResponse.error(message, error);
        response.setStatus(status.value());

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Build error response with error details
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(
            String message,
            ErrorDetails errorDetails,
            HttpStatus status) {

        errorDetails.setStatus(status.value());
        ApiResponse<T> response = ApiResponse.error(message, errorDetails);
        response.setStatus(status.value());

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Build bad request response (HTTP 400)
     */
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
        return error(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Build unauthorized response (HTTP 401)
     */
    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
        return error(message, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Build forbidden response (HTTP 403)
     */
    public static <T> ResponseEntity<ApiResponse<T>> forbidden(String message) {
        return error(message, HttpStatus.FORBIDDEN);
    }

    /**
     * Build not found response (HTTP 404)
     */
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return error(message, HttpStatus.NOT_FOUND);
    }

    /**
     * Build conflict response (HTTP 409)
     */
    public static <T> ResponseEntity<ApiResponse<T>> conflict(String message) {
        return error(message, HttpStatus.CONFLICT);
    }

    /**
     * Build internal server error response (HTTP 500)
     */
    public static <T> ResponseEntity<ApiResponse<T>> internalError(String message) {
        return error(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
