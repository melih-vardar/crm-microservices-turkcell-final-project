package io.github.bothuany.security.service;

import io.github.bothuany.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Security expression bean that provides custom security expression methods
 * to be used in @PreAuthorize annotations across microservices.
 */
@Component("securityExpression")
@RequiredArgsConstructor
public class SecurityExpression {

    private final JwtService jwtService;

    /**
     * Checks if the currently authenticated user has the specified user ID.
     *
     * @param userId User ID to check
     * @return true if the authenticated user has the given ID, false otherwise
     */
    public boolean hasUserId(UUID userId) {
        if (userId == null) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Get token from credentials
        Object credentials = authentication.getCredentials();
        if (credentials instanceof String) {
            String token = (String) credentials;
            try {
                // Try to extract user ID directly from token
                UUID tokenUserId = jwtService.extractUserId(token);
                if (tokenUserId != null) {
                    return userId.equals(tokenUserId);
                }
            } catch (Exception e) {
                // Token parsing failed, continue with other methods
            }
        }

        // Try to get UUID from authentication name
        try {
            UUID principalId = UUID.fromString(authentication.getName());
            return userId.equals(principalId);
        } catch (IllegalArgumentException e) {
            // Name is not a UUID, try other methods
        }

        // Try to extract from principal object
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            try {
                UUID userUuid = UUID.fromString(userDetails.getUsername());
                return userId.equals(userUuid);
            } catch (IllegalArgumentException e) {
                // Username is not a UUID format
            }
        }

        // No matches found
        return false;
    }
}