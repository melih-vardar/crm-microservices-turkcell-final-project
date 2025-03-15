package com.turkcell.crmmicroserviceshw4.userservice.security;

import com.turkcell.crmmicroserviceshw4.userservice.client.JwtServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtServiceClient jwtServiceClient;

    public String generateJwtToken(Authentication authentication) {
        // Assuming the principal's username is what we need
        String username = authentication.getName();
        return jwtServiceClient.generateToken(username).getBody();
    }

    public boolean validateJwtToken(String token) {
        return Boolean.TRUE.equals(jwtServiceClient.validateToken(token).getBody());
    }

    public String getUserNameFromJwtToken(String token) {
        return jwtServiceClient.getUsernameFromToken(token).getBody();
    }

    public boolean invalidateJwtToken(String token) {
        return Boolean.TRUE.equals(jwtServiceClient.invalidateToken(token).getBody());
    }
}