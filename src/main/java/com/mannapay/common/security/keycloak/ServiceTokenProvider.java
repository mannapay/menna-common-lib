package com.mannapay.common.security.keycloak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

/**
 * Provides OAuth2 tokens for service-to-service communication.
 * Uses client credentials grant type to obtain tokens from Keycloak.
 *
 * Based on salon-project implementation pattern.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceTokenProvider {

    private static final String DEFAULT_CLIENT_REGISTRATION_ID = "mannapay-service";

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    /**
     * Get a Bearer token for service-to-service communication.
     *
     * @return Bearer token string including "Bearer " prefix
     * @throws IllegalStateException if token cannot be obtained
     */
    public String getBearerToken() {
        return getBearerToken(DEFAULT_CLIENT_REGISTRATION_ID);
    }

    /**
     * Get a Bearer token for a specific client registration.
     *
     * @param clientRegistrationId the OAuth2 client registration ID
     * @return Bearer token string including "Bearer " prefix
     * @throws IllegalStateException if token cannot be obtained
     */
    public String getBearerToken(String clientRegistrationId) {
        try {
            var principal = new AnonymousAuthenticationToken(
                    "service",
                    "service",
                    AuthorityUtils.createAuthorityList("ROLE_SERVICE")
            );

            OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest
                    .withClientRegistrationId(clientRegistrationId)
                    .principal(principal)
                    .build();

            OAuth2AuthorizedClient client = authorizedClientManager.authorize(request);

            if (client == null || client.getAccessToken() == null) {
                log.error("Failed to obtain access token for client: {}", clientRegistrationId);
                throw new IllegalStateException("Failed to obtain client credentials access token");
            }

            String token = client.getAccessToken().getTokenValue();
            log.debug("Successfully obtained access token for client: {}", clientRegistrationId);
            return "Bearer " + token;

        } catch (Exception e) {
            log.error("Error obtaining OAuth2 token for client {}: {}", clientRegistrationId, e.getMessage());
            throw new IllegalStateException("Failed to obtain client credentials access token", e);
        }
    }

    /**
     * Get just the token value without the "Bearer " prefix.
     *
     * @return raw token value
     */
    public String getTokenValue() {
        return getTokenValue(DEFAULT_CLIENT_REGISTRATION_ID);
    }

    /**
     * Get just the token value without the "Bearer " prefix for a specific client.
     *
     * @param clientRegistrationId the OAuth2 client registration ID
     * @return raw token value
     */
    public String getTokenValue(String clientRegistrationId) {
        String bearerToken = getBearerToken(clientRegistrationId);
        return bearerToken.substring(7); // Remove "Bearer " prefix
    }
}
