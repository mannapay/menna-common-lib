package com.mannapay.common.security.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for JWT authentication containing access and refresh tokens.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "JWT authentication response")
public class JwtResponse {

    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "Token type", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Access token expiry time in seconds", example = "3600")
    private Long expiresIn;

    @Schema(description = "Refresh token expiry time in seconds", example = "604800")
    private Long refreshExpiresIn;

    @Schema(description = "User ID", example = "12345")
    private Long userId;

    @Schema(description = "User email", example = "user@example.com")
    private String email;

    @Schema(description = "User full name", example = "John Doe")
    private String fullName;

    @Schema(description = "User roles", example = "[\"ROLE_USER\"]")
    private Set<String> roles;

    @Schema(description = "Whether 2FA is enabled", example = "true")
    private Boolean twoFactorEnabled;

    @Schema(description = "Whether 2FA verification is required", example = "false")
    private Boolean requiresTwoFactor;

    @Schema(description = "Temporary token for 2FA verification (if required)")
    private String tempToken;

    @Schema(description = "Token issue timestamp")
    private LocalDateTime issuedAt;

    @Schema(description = "Token expiry timestamp")
    private LocalDateTime expiresAt;
}
