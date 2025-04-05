package com.turkcell.customer_service.util;

import com.turkcell.customer_service.service.TokenValidationService;
import io.github.bothuany.exception.BusinessException;
import io.github.bothuany.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Utility class for handling authentication and authorization in controllers
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationUtil {

    private final TokenValidationService tokenValidationService;

    /**
     * Gets the authenticated user's ID from the token
     * 
     * @param token JWT token with "Bearer " prefix
     * @return User ID
     * @throws UnauthorizedException if token is invalid
     */
    public UUID getAuthenticatedUserId(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Authorization token is missing");
        }

        UUID userId = tokenValidationService.getUserIdFromToken(token);
        if (userId == null) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        return userId;
    }

    /**
     * Checks if the token is valid
     * 
     * @param token JWT token with "Bearer " prefix
     * @return true if valid, throws exception otherwise
     * @throws UnauthorizedException if token is invalid
     */
    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Authorization token is missing");
        }

        boolean valid = tokenValidationService.validateToken(token);
        if (!valid) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        return true;
    }

    /**
     * Gets the authenticated username from the token
     * 
     * @param token JWT token with "Bearer " prefix
     * @return Username
     * @throws UnauthorizedException if token is invalid
     */
    public String getAuthenticatedUsername(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Authorization token is missing");
        }

        String username = tokenValidationService.getUsernameFromToken(token);
        if (username == null) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        return username;
    }
}