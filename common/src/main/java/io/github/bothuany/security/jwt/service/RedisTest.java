package io.github.bothuany.security.jwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Test class for verifying Redis connectivity at startup.
 * Only runs when the "redis-test" profile is active.
 */
@Configuration
@Profile("redis-test")
@Slf4j
@RequiredArgsConstructor
public class RedisTest {

    private final TokenBlacklistService tokenBlacklistService;
    private final RedisTemplate<String, String> redisTemplate;

    @Bean
    public CommandLineRunner testRedis() {
        return args -> {
            boolean redisAvailable = redisTemplate != null;

            log.info("=== REDIS TEST ===");
            log.info("Redis available: {}", redisAvailable);

            if (redisAvailable) {
                try {
                    // Try a simple Redis operation
                    redisTemplate.opsForValue().set("test-key", "test-value");
                    String value = redisTemplate.opsForValue().get("test-key");
                    log.info("Redis test successful! Value: {}", value);

                    // Test token blacklist
                    String testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjQ2ODU5NzM5MjJ9.uh0QtPmYxVayVNO-XWZJWgO-8dmupGXh4tPlEKxVNQM";
                    tokenBlacklistService.blacklistToken(testToken);
                    boolean blacklisted = tokenBlacklistService.isTokenBlacklisted(testToken);
                    log.info("Token blacklisted: {}", blacklisted);

                } catch (Exception e) {
                    log.error("Redis test failed: {}", e.getMessage());
                }
            } else {
                log.warn("Redis is not available, using fallback in-memory storage");

                // Test in-memory fallback
                String testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjQ2ODU5NzM5MjJ9.uh0QtPmYxVayVNO-XWZJWgO-8dmupGXh4tPlEKxVNQM";
                tokenBlacklistService.blacklistToken(testToken);
                boolean blacklisted = tokenBlacklistService.isTokenBlacklisted(testToken);
                log.info("Token blacklisted (in-memory): {}", blacklisted);
            }

            log.info("=== REDIS TEST COMPLETED ===");
        };
    }
}
