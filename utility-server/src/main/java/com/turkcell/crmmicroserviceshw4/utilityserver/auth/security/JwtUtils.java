package com.turkcell.crmmicroserviceshw4.utilityserver.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    // Set to store invalidated tokens
    private final Set<String> invalidatedTokens = new HashSet<>();

    public String generateJwtToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            // Check if token is invalidated
            if (invalidatedTokens.contains(authToken)) {
                logger.info("Token is invalidated: {}", authToken);
                return false;
            }

            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public boolean invalidateToken(String token) {
        try {
            // Validate token first
            if (!validateJwtToken(token)) {
                return false;
            }

            // Add to invalidated tokens set
            invalidatedTokens.add(token);

            // Clean up expired tokens from the set periodically
            cleanupInvalidatedTokens();

            return true;
        } catch (Exception e) {
            logger.error("Error invalidating token: {}", e.getMessage());
            return false;
        }
    }

    private void cleanupInvalidatedTokens() {
        // Remove expired tokens from the invalidated tokens set
        invalidatedTokens.removeIf(token -> {
            try {
                Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
                return false; // Token is still valid, keep it in the set
            } catch (ExpiredJwtException e) {
                return true; // Token is expired, remove it from the set
            } catch (Exception e) {
                return true; // Any other exception, remove it from the set
            }
        });
    }
}