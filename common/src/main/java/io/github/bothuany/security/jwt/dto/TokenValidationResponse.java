package io.github.bothuany.security.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Token validation response that contains information about a validated JWT
 * token.
 * Used by services to validate tokens and get user information from them.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse {
    private boolean valid;
    private String username;
    private UUID userId;
    private List<String> roles;
}