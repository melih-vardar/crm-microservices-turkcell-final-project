package com.turkcell.crmmicroserviceshw4.utilityserver.auth.controller;

import com.turkcell.crmmicroserviceshw4.utilityserver.auth.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jwt")
@RequiredArgsConstructor
public class JwtController {

    private final JwtUtils jwtUtils;

    @PostMapping("/generate")
    public ResponseEntity<String> generateToken(@RequestParam String username) {
        String token = jwtUtils.generateJwtToken(username);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        // Remove "Bearer " prefix if present
        String cleanToken = cleanToken(token);
        boolean isValid = jwtUtils.validateJwtToken(cleanToken);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/username")
    public ResponseEntity<String> getUsernameFromToken(@RequestParam String token) {
        try {
            // Remove "Bearer " prefix if present
            String cleanToken = cleanToken(token);
            String username = jwtUtils.getUserNameFromJwtToken(cleanToken);
            return ResponseEntity.ok(username);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }

    @PostMapping("/invalidate")
    public ResponseEntity<Boolean> invalidateToken(@RequestParam String token) {
        // Remove "Bearer " prefix if present
        String cleanToken = cleanToken(token);
        boolean success = jwtUtils.invalidateToken(cleanToken);
        return ResponseEntity.ok(success);
    }

    /**
     * Removes the "Bearer " prefix from the token if present
     */
    private String cleanToken(String token) {
        if (StringUtils.hasText(token) && token.toLowerCase().startsWith("bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}