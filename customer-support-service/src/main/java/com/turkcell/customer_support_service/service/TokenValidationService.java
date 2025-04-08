package com.turkcell.customer_support_service.service;

import com.turkcell.customer_support_service.client.UserServiceClient;
import io.github.bothuany.security.jwt.dto.TokenValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for validating tokens with the user-service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenValidationService {

    private final UserServiceClient userServiceClient;

    /**
     * Validates a token with the user-service
     * 
     * @param token JWT token with "Bearer " prefix
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            log.info("Validating token with user-service");

            // First, try using the test endpoint to make sure the service is reachable
            try {
                userServiceClient.testAuth();
                log.info("Successfully connected to user-service test endpoint");
            } catch (Exception e) {
                log.error("Failed to connect to user-service test endpoint: {}", e.getMessage());
            }

            ResponseEntity<TokenValidationResponse> response = userServiceClient.validateToken(token);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Token validation successful, valid={}", response.getBody().isValid());
                return response.getBody().isValid();
            }

            log.warn("Token validation failed with status: {}", response.getStatusCode());
            return false;
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Extracts user ID from a valid token
     * 
     * @param token JWT token with "Bearer " prefix
     * @return User ID if token is valid, null otherwise
     */
    public UUID extractUserId(String token) {
        try {
            ResponseEntity<TokenValidationResponse> response = userServiceClient.validateToken(token);

            if (response.getStatusCode().is2xxSuccessful() &&
                    response.getBody() != null &&
                    response.getBody().isValid()) {
                return response.getBody().getUserId();
            }

            return null;
        } catch (Exception e) {
            log.error("Error extracting user ID from token: {}", e.getMessage(), e);
            return null;
        }
    }
}