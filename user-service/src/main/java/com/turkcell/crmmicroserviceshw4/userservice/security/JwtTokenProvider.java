package com.turkcell.crmmicroserviceshw4.userservice.security;

import io.github.bothuany.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtService jwtService;

    public String generateJwtToken(Authentication authentication) {
        return jwtService.generateToken(authentication);
    }

    public boolean validateJwtToken(String token) {
        return jwtService.validateToken(token);
    }

    public String getUserNameFromJwtToken(String token) {
        return jwtService.extractUsername(token);
    }

    public boolean invalidateJwtToken(String token) {
        // JwtService'deki invalidateToken metodunu kullanarak token'ı geçersiz kıl
        return jwtService.invalidateToken(token);
    }
}