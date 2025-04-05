package io.github.bothuany.security.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of TokenBlacklistService.
 * Used as a fallback when Redis is not available.
 */
@Slf4j
public class InMemoryTokenBlacklistService implements TokenBlacklistService {

    private final Map<String, LocalDateTime> blacklist = new ConcurrentHashMap<>();

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secret;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(keyBytes);
        log.info("InMemoryTokenBlacklistService initialized");
    }

    @Override
    public void blacklistToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();

            if (expiration != null) {
                LocalDateTime expiryDate = expiration.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                blacklist.put(token, expiryDate);
                log.info("Token blacklisted in memory with expiry date: {}", expiryDate);
            } else {
                log.warn("Could not blacklist token as expiration date could not be extracted");
            }
        } catch (Exception e) {
            log.error("Error blacklisting token: {}", e.getMessage());
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        try {
            return blacklist.containsKey(token) &&
                    LocalDateTime.now().isBefore(blacklist.get(token));
        } catch (Exception e) {
            log.error("Error checking if token is blacklisted: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Clean up expired tokens from the blacklist.
     * Runs every hour.
     */
    @Scheduled(fixedDelay = 3600000) // Run every hour
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        int count = 0;

        for (Map.Entry<String, LocalDateTime> entry : blacklist.entrySet()) {
            if (now.isAfter(entry.getValue())) {
                blacklist.remove(entry.getKey());
                count++;
            }
        }

        if (count > 0) {
            log.info("Cleaned up {} expired tokens from in-memory blacklist", count);
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}