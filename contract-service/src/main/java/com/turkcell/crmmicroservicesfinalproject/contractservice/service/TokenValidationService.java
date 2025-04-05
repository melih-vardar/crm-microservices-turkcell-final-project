package com.turkcell.crmmicroservicesfinalproject.contractservice.service;

import com.turkcell.crmmicroservicesfinalproject.contractservice.client.UserServiceClient;
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
            ResponseEntity<TokenValidationResponse> response = userServiceClient.validateToken(token);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().isValid();
            }

            return false;
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts user ID from a valid token
     * 
     * @param token JWT token with "Bearer " prefix
     * @return User ID if token is valid, null otherwise
     */
    public UUID getUserIdFromToken(String token) {
        try {
            ResponseEntity<TokenValidationResponse> response = userServiceClient.validateToken(token);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                    && response.getBody().isValid()) {
                return response.getBody().getUserId();
            }

            return null;
        } catch (Exception e) {
            log.error("Error extracting user ID from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extracts username from a valid token
     * 
     * @param token JWT token with "Bearer " prefix
     * @return Username if token is valid, null otherwise
     */
    public String getUsernameFromToken(String token) {
        try {
            ResponseEntity<TokenValidationResponse> response = userServiceClient.validateToken(token);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                    && response.getBody().isValid()) {
                return response.getBody().getUsername();
            }

            return null;
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }
}