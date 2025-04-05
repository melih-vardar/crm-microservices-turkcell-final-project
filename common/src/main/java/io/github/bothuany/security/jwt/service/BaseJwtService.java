package io.github.bothuany.security.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Base JWT service providing token operations for authentication.
 * Handles token generation, validation, and content extraction.
 * Uses TokenBlacklistService for token invalidation.
 */
@Service
@Slf4j
public class BaseJwtService {

    @Value("${jwt.expiration:6000000}")
    private Long expirationTimeMs;

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    private Key signingKey;

    private final ApplicationContext applicationContext;

    public BaseJwtService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate a token for a user with username only.
     * 
     * @param username the username
     * @return the generated JWT token
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Generate a token for a user with username and user ID.
     * 
     * @param username the username
     * @param userId   the user ID
     * @return the generated JWT token
     */
    public String generateToken(String username, UUID userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        return createToken(claims, username);
    }

    /**
     * Generate a token for a user with username, user ID, and roles.
     * 
     * @param username the username
     * @param userId   the user ID
     * @param roles    the user roles
     * @return the generated JWT token
     */
    public String generateToken(String username, UUID userId, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("roles", roles);
        return createToken(claims, username);
    }

    /**
     * Create a token with the provided claims and subject.
     * 
     * @param claims  the claims to include in the token
     * @param subject the subject (username)
     * @return the generated JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate if a token is valid.
     * Checks if the token is blacklisted or expired.
     * 
     * @param token the token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            // First check if the token is blacklisted
            if (isTokenBlacklisted(token)) {
                return false;
            }

            // Then check if the token is expired
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if a token is blacklisted.
     * 
     * @param token the token to check
     * @return true if the token is blacklisted, false otherwise
     */
    private boolean isTokenBlacklisted(String token) {
        try {
            TokenBlacklistService tokenBlacklistService = applicationContext.getBean(TokenBlacklistService.class);
            return tokenBlacklistService.isTokenBlacklisted(token);
        } catch (Exception e) {
            log.error("Error checking if token is blacklisted: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract the username from a token.
     * 
     * @param token the token
     * @return the username
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extract the user ID from a token.
     * 
     * @param token the token
     * @return the user ID, or null if not present
     */
    public UUID extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        String userIdStr = claims.get("userId", String.class);
        return userIdStr != null ? UUID.fromString(userIdStr) : null;
    }

    /**
     * Extract the roles from a token.
     * 
     * @param token the token
     * @return the list of roles
     */
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    /**
     * Invalidate a token by adding it to the blacklist.
     * 
     * @param token the token to invalidate
     * @return true if the token was invalidated
     */
    public boolean invalidateToken(String token) {
        try {
            TokenBlacklistService tokenBlacklistService = applicationContext.getBean(TokenBlacklistService.class);
            tokenBlacklistService.blacklistToken(token);
            return true;
        } catch (Exception e) {
            log.error("Error invalidating token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract all claims from a token.
     * 
     * @param token the token
     * @return the claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
