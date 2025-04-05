package io.github.bothuany.security.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Service for token blacklisting.
 * Uses Redis for distributed blacklist storage if available, or falls back to
 * in-memory storage.
 * Periodically cleans up expired tokens from both storages.
 */
@Slf4j
public class RedisTokenBlacklistService implements TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private boolean redisAvailable = false;

    // In-memory fallback storage for blacklisted tokens when Redis is unavailable
    private final Map<String, LocalDateTime> inMemoryBlacklist = new ConcurrentHashMap<>();

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secret;

    private Key key;

    public RedisTokenBlacklistService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        if (redisTemplate != null) {
            try {
                // Test Redis connectivity
                redisTemplate.getConnectionFactory().getConnection().ping();
                redisAvailable = true;
                log.info("TokenBlacklistService initialized with Redis available");
            } catch (Exception e) {
                log.warn("Redis connection test failed: {}. Will use in-memory fallback.", e.getMessage());
                redisAvailable = false;
            }
        } else {
            log.warn("Redis template is null. Will use in-memory fallback.");
        }
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Blacklist a token.
     * If Redis is available, store in Redis with TTL.
     * Otherwise, store in memory.
     *
     * @param token the token to blacklist
     */
    @Override
    public void blacklistToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();

            if (expiration != null) {
                LocalDateTime expiryDate = expiration.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                // First try Redis if available
                if (useRedis()) {
                    try {
                        long ttlSeconds = Duration.between(LocalDateTime.now(), expiryDate).getSeconds();
                        if (ttlSeconds > 0) {
                            redisTemplate.opsForValue().set("blacklisted:" + token, "true", ttlSeconds,
                                    TimeUnit.SECONDS);
                            log.debug("Token blacklisted in Redis with TTL of {} seconds", ttlSeconds);
                            return;
                        }
                    } catch (Exception e) {
                        log.warn("Failed to blacklist token in Redis: {}. Falling back to in-memory storage.",
                                e.getMessage());
                        redisAvailable = false;
                    }
                }

                // Fallback to in-memory
                inMemoryBlacklist.put(token, expiryDate);
                log.debug("Token blacklisted in memory with expiry date: {}", expiryDate);
            } else {
                log.warn("Could not blacklist token as expiration date could not be extracted");
            }
        } catch (Exception e) {
            log.error("Error blacklisting token: {}", e.getMessage());
        }
    }

    /**
     * Check if a token is blacklisted.
     * Checks Redis first if available, then falls back to in-memory storage.
     *
     * @param token the token to check
     * @return true if the token is blacklisted, false otherwise
     */
    @Override
    public boolean isTokenBlacklisted(String token) {
        try {
            // First try Redis if available
            if (useRedis()) {
                try {
                    Boolean result = redisTemplate.hasKey("blacklisted:" + token);
                    return result != null && result;
                } catch (Exception e) {
                    log.warn("Failed to check token in Redis: {}. Falling back to in-memory check.", e.getMessage());
                    redisAvailable = false;
                }
            }

            // Fallback to in-memory
            return inMemoryBlacklist.containsKey(token) &&
                    LocalDateTime.now().isBefore(inMemoryBlacklist.get(token));
        } catch (Exception e) {
            log.error("Error checking if token is blacklisted: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Clean up expired tokens from the in-memory blacklist.
     * Runs every hour.
     */
    @Scheduled(fixedDelay = 3600000) // Run every hour
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();

        // Clean up in-memory blacklist
        int inMemoryCount = 0;
        for (Map.Entry<String, LocalDateTime> entry : inMemoryBlacklist.entrySet()) {
            if (now.isAfter(entry.getValue())) {
                inMemoryBlacklist.remove(entry.getKey());
                inMemoryCount++;
            }
        }

        if (inMemoryCount > 0) {
            log.info("Cleaned up {} expired tokens from in-memory blacklist", inMemoryCount);
        }

        // Try to reconnect to Redis if it was previously unavailable
        if (!redisAvailable && redisTemplate != null) {
            try {
                redisTemplate.getConnectionFactory().getConnection().ping();
                redisAvailable = true;
                log.info("Redis connectivity restored");
            } catch (Exception e) {
                // Still unavailable, will continue to use in-memory storage
            }
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean useRedis() {
        return redisAvailable && redisTemplate != null;
    }
}