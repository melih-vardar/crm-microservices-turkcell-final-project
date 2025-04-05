package io.github.bothuany.security.config;

import io.github.bothuany.security.jwt.service.InMemoryTokenBlacklistService;
import io.github.bothuany.security.jwt.service.RedisTokenBlacklistService;
import io.github.bothuany.security.jwt.service.TokenBlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis configuration for token blacklist management.
 * Provides a Redis-based token blacklist service with a fallback to in-memory
 * storage
 * when Redis is not available or disabled.
 */
@Configuration
@Slf4j
public class RedisConfig {

    /**
     * Creates a custom RedisTemplate for token blacklist operations.
     * Uses Spring Boot's auto-configured RedisConnectionFactory.
     */
    @Bean("tokenBlacklistRedisTemplate")
    @ConditionalOnProperty(name = "spring.redis.enabled", havingValue = "true", matchIfMissing = true)
    public RedisTemplate<String, String> tokenBlacklistRedisTemplate(
            @Autowired RedisConnectionFactory connectionFactory) {
        try {
            log.info("Creating tokenBlacklistRedisTemplate with provided RedisConnectionFactory");
            RedisTemplate<String, String> template = new RedisTemplate<>();
            template.setConnectionFactory(connectionFactory);
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new StringRedisSerializer());
            template.setHashKeySerializer(new StringRedisSerializer());
            template.setHashValueSerializer(new StringRedisSerializer());
            template.afterPropertiesSet();
            log.info("TokenBlacklist RedisTemplate created successfully");
            return template;
        } catch (Exception e) {
            log.error("Failed to create tokenBlacklistRedisTemplate: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Primary token blacklist service that uses Redis when available.
     */
    @Bean("tokenBlacklistService")
    @Primary
    @ConditionalOnProperty(name = "spring.redis.enabled", havingValue = "true", matchIfMissing = true)
    public TokenBlacklistService redisTokenBlacklistServiceBean(
            @Qualifier("tokenBlacklistRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        if (redisTemplate == null) {
            log.warn("Redis template is null, falling back to in-memory token blacklist service");
            return inMemoryTokenBlacklistServiceBean();
        }

        log.info("Creating Redis-based TokenBlacklistService");
        return new RedisTokenBlacklistService(redisTemplate);
    }

    /**
     * Fallback token blacklist service that uses in-memory storage.
     * Used when Redis is disabled or unavailable.
     */
    @Bean("inMemoryTokenBlacklistService")
    @ConditionalOnMissingBean(name = "tokenBlacklistService")
    public TokenBlacklistService inMemoryTokenBlacklistServiceBean() {
        log.info("Creating InMemory-based TokenBlacklistService (fallback)");
        return new InMemoryTokenBlacklistService();
    }
}