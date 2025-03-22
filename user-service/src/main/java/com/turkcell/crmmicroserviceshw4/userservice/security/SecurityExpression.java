package com.turkcell.crmmicroserviceshw4.userservice.security;

import com.turkcell.crmmicroserviceshw4.userservice.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Security expression bean that provides custom security expression methods
 * to be used in @PreAuthorize annotations.
 */
@Component("securityExpression")
public class SecurityExpression {

    /**
     * Checks if the currently authenticated user has the specified user ID.
     *
     * @param userId User ID to check
     * @return true if the authenticated user has the given ID, false otherwise
     */
    public boolean hasUserId(UUID userId) {
        if (userId == null) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return false;
        }

        User user = (User) principal;
        return userId.equals(user.getId());
    }
}