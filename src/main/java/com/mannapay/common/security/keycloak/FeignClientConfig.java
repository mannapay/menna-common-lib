package com.mannapay.common.security.keycloak;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

/**
 * Configuration for Feign clients to add OAuth2 authentication headers.
 * Enables secure service-to-service communication using Keycloak tokens.
 * Services can override by defining their own RequestInterceptor beans.
 *
 * Based on salon-project implementation pattern.
 */
@Configuration("commonFeignClientConfig")
@RequiredArgsConstructor
public class FeignClientConfig {

    private static final Logger log = LoggerFactory.getLogger(FeignClientConfig.class);

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Value("${spring.application.name:unknown-service}")
    private String serviceName;

    /**
     * Request interceptor that adds OAuth2 token and service identification headers
     * to all outgoing Feign client requests.
     */
    @Bean("commonServiceAuthRequestInterceptor")
    @ConditionalOnMissingBean(name = "serviceAuthRequestInterceptor")
    public RequestInterceptor serviceAuthRequestInterceptor() {
        return template -> {
            // Add service identification header
            template.header("X-Service-Name", serviceName);

            try {
                var authorizeRequest = OAuth2AuthorizeRequest
                        .withClientRegistrationId("mannapay-service")
                        .principal("service-account-" + serviceName)
                        .build();

                OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

                if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
                    String tokenValue = authorizedClient.getAccessToken().getTokenValue();
                    template.header("Authorization", "Bearer " + tokenValue);
                    log.debug("Added OAuth2 token to request: {} -> {}", serviceName, template.url());
                } else {
                    log.warn("No OAuth2 token available for request from {} to: {}", serviceName, template.url());
                }
            } catch (Exception e) {
                log.error("Error obtaining OAuth2 token for service {}: {}", serviceName, e.getMessage());
                // Don't fail the request - let the target service handle unauthorized requests
            }
        };
    }

    /**
     * Request interceptor that only adds service identification without OAuth2 token.
     * Use this for internal service calls that don't require authentication.
     */
    @Bean("commonInternalServiceInterceptor")
    @ConditionalOnMissingBean(name = "internalServiceInterceptor")
    public RequestInterceptor internalServiceInterceptor() {
        return template -> {
            template.header("X-Service-Name", serviceName);
            template.header("X-Internal-Call", "true");
        };
    }
}
