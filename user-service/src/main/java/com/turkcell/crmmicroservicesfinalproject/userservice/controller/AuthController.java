package com.turkcell.crmmicroservicesfinalproject.userservice.controller;

import com.turkcell.crmmicroservicesfinalproject.userservice.security.JwtTokenProvider;
import com.turkcell.crmmicroservicesfinalproject.userservice.service.UserService;
import io.github.bothuany.dtos.user.JwtResponseDTO;
import io.github.bothuany.dtos.user.UserLoginDTO;
import io.github.bothuany.dtos.user.UserRegisterDTO;
import io.github.bothuany.security.jwt.dto.LoginRequest;
import io.github.bothuany.security.jwt.dto.TokenResponse;
import io.github.bothuany.security.jwt.dto.TokenValidationResponse;
import io.github.bothuany.security.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Authentication controller for the user-service.
 * This is the only auth controller in the system.
 * Other services should use Feign clients to communicate with this controller.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Simple test endpoint to verify routing is working
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        log.info("Test endpoint accessed in AuthController");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Auth test endpoint is working");
        response.put("service", "user-service");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request received for: {}", loginRequest.getUsername());
        TokenResponse tokenResponse = authenticationService.authenticate(loginRequest);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/login-with-email")
    public ResponseEntity<JwtResponseDTO> loginWithEmail(@Valid @RequestBody UserLoginDTO loginDTO) {
        log.info("Login with email request received");
        JwtResponseDTO response = userService.login(loginDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponseDTO> register(@Valid @RequestBody UserRegisterDTO registerRequest) {
        log.info("Register request received");
        JwtResponseDTO response = userService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        log.info("Logout request received");
        // Extract token from Authorization header
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        boolean result = userService.logout(token);
        if (result) {
            return ResponseEntity.ok().body("Logout successful");
        } else {
            return ResponseEntity.badRequest().body("Logout failed");
        }
    }

    /**
     * Token doğrulama endpointi.
     * Diğer servisler bu endpointi kullanarak bir token'ın geçerli olup olmadığını,
     * ve eğer geçerliyse token içindeki kullanıcı bilgilerini alabilirler.
     * 
     * @param token Doğrulanacak JWT token
     * @return Token geçerliyse kullanıcı bilgilerini, geçerli değilse hata döner
     */
    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestHeader("Authorization") String token) {
        log.info("Token validation request received");

        // Extract token from Authorization header
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Validate token
        boolean isValid = jwtTokenProvider.validateJwtToken(token);
        log.info("Token is valid: {}", isValid);

        if (!isValid) {
            return ResponseEntity.badRequest().body(
                    TokenValidationResponse.builder()
                            .valid(false)
                            .build());
        }

        // Extract user info
        String username = jwtTokenProvider.getUserNameFromJwtToken(token);
        UUID userId = jwtTokenProvider.getUserIdFromJwtToken(token);
        log.info("Extracted username: {}, userId: {}", username, userId);

        TokenValidationResponse response = TokenValidationResponse.builder()
                .valid(true)
                .username(username)
                .userId(userId)
                .build();

        return ResponseEntity.ok(response);
    }
}