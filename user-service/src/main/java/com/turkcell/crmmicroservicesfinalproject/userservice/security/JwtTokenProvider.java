package com.turkcell.crmmicroservicesfinalproject.userservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtService jwtService;

    public String generateJwtToken(Authentication authentication) {
        return jwtService.generateToken(authentication);
    }

    public String generateJwtToken(Authentication authentication, UUID userId) {
        return jwtService.generateToken(authentication, userId);
    }

    public String generateJwtTokenWithRoles(Authentication authentication, UUID userId) {
        return jwtService.generateTokenWithRoles(authentication, userId);
    }

    public boolean validateJwtToken(String token) {
        return jwtService.validateToken(token);
    }

    public String getUserNameFromJwtToken(String token) {
        return jwtService.extractUsername(token);
    }

    public UUID getUserIdFromJwtToken(String token) {
        return jwtService.extractUserId(token);
    }

    public List<String> getRolesFromJwtToken(String token) {
        return jwtService.extractRoles(token);
    }

    public boolean invalidateJwtToken(String token) {
        // JwtService'deki invalidateToken metodunu kullanarak token'ı geçersiz kıl
        return jwtService.invalidateToken(token);
    }
}