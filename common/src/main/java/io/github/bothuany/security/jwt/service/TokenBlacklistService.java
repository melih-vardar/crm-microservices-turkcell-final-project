package io.github.bothuany.security.jwt.service;

/**
 * Interface for token blacklisting operations.
 * Implementations may use different storage mechanisms like Redis or in-memory
 * maps.
 */
public interface TokenBlacklistService {

    /**
     * Blacklist a token.
     * 
     * @param token the token to blacklist
     */
    void blacklistToken(String token);

    /**
     * Check if a token is blacklisted.
     * 
     * @param token the token to check
     * @return true if the token is blacklisted, false otherwise
     */
    boolean isTokenBlacklisted(String token);
}