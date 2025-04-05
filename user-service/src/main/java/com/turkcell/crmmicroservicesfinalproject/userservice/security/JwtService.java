package com.turkcell.crmmicroservicesfinalproject.userservice.security;

import io.github.bothuany.security.jwt.service.BaseJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JWT service implementation for user-service that handles authentication
 * objects
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final BaseJwtService jwtService;

    /**
     * Generate a token from an Authentication object
     * 
     * @param authentication the authentication object
     * @return JWT token
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtService.generateToken(userDetails.getUsername());
    }

    /**
     * Generate a token from an Authentication object with user ID
     * 
     * @param authentication the authentication object
     * @param userId         the user ID
     * @return JWT token
     */
    public String generateToken(Authentication authentication, UUID userId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtService.generateToken(userDetails.getUsername(), userId);
    }

    /**
     * Generate a token from an Authentication object with user ID and roles
     * 
     * @param authentication the authentication object
     * @param userId         the user ID
     * @return JWT token
     */
    public String generateToken(Authentication authentication, UUID userId, List<String> roles) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtService.generateToken(userDetails.getUsername(), userId, roles);
    }

    /**
     * Generate a token that includes roles extracted from the Authentication object
     * 
     * @param authentication the authentication object
     * @param userId         the user ID
     * @return JWT token
     */
    public String generateTokenWithRoles(Authentication authentication, UUID userId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return jwtService.generateToken(userDetails.getUsername(), userId, roles);
    }

    /**
     * Validate a token
     * 
     * @param token JWT token
     * @return true if valid
     */
    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }

    /**
     * Extract username from token
     * 
     * @param token JWT token
     * @return username
     */
    public String extractUsername(String token) {
        return jwtService.extractUsername(token);
    }

    /**
     * Extract user ID from token
     * 
     * @param token JWT token
     * @return user ID
     */
    public UUID extractUserId(String token) {
        return jwtService.extractUserId(token);
    }

    /**
     * Extract roles from token
     * 
     * @param token JWT token
     * @return list of roles
     */
    public List<String> extractRoles(String token) {
        return jwtService.extractRoles(token);
    }

    /**
     * Invalidate a token
     * 
     * @param token JWT token
     * @return true if invalidated
     */
    public boolean invalidateToken(String token) {
        return jwtService.invalidateToken(token);
    }
}