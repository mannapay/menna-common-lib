package com.mannapay.common.security.keycloak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converts Keycloak JWT tokens to Spring Security Authentication tokens.
 * Extracts roles from both realm_access and resource_access claims.
 *
 * Based on salon-project implementation pattern.
 */
@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String REALM_ACCESS = "realm_access";
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLES = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Value("${spring.security.oauth2.resourceserver.jwt.resource-id:}")
    private String resourceId;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
    }

    /**
     * Extract roles from both realm_access and resource_access claims.
     * Realm roles are global across all clients.
     * Resource roles are specific to a client/service.
     */
    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Set<GrantedAuthority> allRoles = new HashSet<>();
        allRoles.addAll(extractRealmRoles(jwt));
        allRoles.addAll(extractClientRoles(jwt));
        return allRoles;
    }

    /**
     * Extract realm roles from JWT token.
     * These are global roles defined at the Keycloak realm level.
     */
    private Set<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS);
        if (realmAccess == null || !realmAccess.containsKey(ROLES)) {
            return Collections.emptySet();
        }

        @SuppressWarnings("unchecked")
        Collection<String> roles = (Collection<String>) realmAccess.get(ROLES);
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.toUpperCase()))
                .collect(Collectors.toSet());
    }

    /**
     * Extract client-specific roles from JWT token.
     * These are roles specific to the current service/client.
     */
    private Set<GrantedAuthority> extractClientRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim(RESOURCE_ACCESS);
        if (resourceAccess == null) {
            return Collections.emptySet();
        }

        // If resourceId is not configured, try to extract from all clients
        if (resourceId == null || resourceId.isEmpty()) {
            return extractAllClientRoles(resourceAccess);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get(resourceId);
        if (clientAccess == null || !clientAccess.containsKey(ROLES)) {
            return Collections.emptySet();
        }

        @SuppressWarnings("unchecked")
        Collection<String> roles = (Collection<String>) clientAccess.get(ROLES);
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.toUpperCase()))
                .collect(Collectors.toSet());
    }

    /**
     * Extract roles from all clients in resource_access.
     */
    @SuppressWarnings("unchecked")
    private Set<GrantedAuthority> extractAllClientRoles(Map<String, Object> resourceAccess) {
        Set<GrantedAuthority> allRoles = new HashSet<>();
        for (Object clientAccess : resourceAccess.values()) {
            if (clientAccess instanceof Map) {
                Map<String, Object> access = (Map<String, Object>) clientAccess;
                if (access.containsKey(ROLES)) {
                    Collection<String> roles = (Collection<String>) access.get(ROLES);
                    roles.stream()
                            .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.toUpperCase()))
                            .forEach(allRoles::add);
                }
            }
        }
        return allRoles;
    }

    /**
     * Get the principal claim name from the JWT token.
     * Prefers 'preferred_username', falls back to 'sub'.
     */
    private String getPrincipalClaimName(Jwt jwt) {
        String preferredUsername = jwt.getClaim("preferred_username");
        if (preferredUsername != null && !preferredUsername.isEmpty()) {
            return preferredUsername;
        }
        return jwt.getSubject();
    }

    /**
     * Get the user ID (subject) from the JWT token.
     */
    public static String getUserId(Jwt jwt) {
        return jwt.getSubject();
    }

    /**
     * Get the email from the JWT token.
     */
    public static String getEmail(Jwt jwt) {
        return jwt.getClaim("email");
    }

    /**
     * Get the full name from the JWT token.
     */
    public static String getFullName(Jwt jwt) {
        return jwt.getClaim("name");
    }

    /**
     * Check if the JWT token has a specific role.
     */
    public static boolean hasRole(Jwt jwt, String role) {
        Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS);
        if (realmAccess != null && realmAccess.containsKey(ROLES)) {
            @SuppressWarnings("unchecked")
            Collection<String> roles = (Collection<String>) realmAccess.get(ROLES);
            return roles.contains(role) || roles.contains(role.toUpperCase());
        }
        return false;
    }
}
