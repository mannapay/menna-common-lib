package com.mannapay.common.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for JWT authentication (login).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT authentication request")
public class JwtRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "User email address", example = "user@example.com", required = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "User password", example = "SecurePass123!", required = true)
    private String password;

    @Schema(description = "Device ID for trusted device tracking", example = "device-12345")
    private String deviceId;

    @Schema(description = "Device name", example = "iPhone 13 Pro")
    private String deviceName;

    @Schema(description = "Remember this device for future logins", example = "true")
    @Builder.Default
    private Boolean rememberDevice = false;
}
