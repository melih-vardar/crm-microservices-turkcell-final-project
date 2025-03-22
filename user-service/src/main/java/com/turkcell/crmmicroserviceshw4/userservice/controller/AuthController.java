package com.turkcell.crmmicroserviceshw4.userservice.controller;

import com.turkcell.crmmicroserviceshw4.userservice.service.UserService;
import io.github.bothuany.dtos.user.JwtResponseDTO;
import io.github.bothuany.dtos.user.UserLoginDTO;
import io.github.bothuany.dtos.user.UserRegisterDTO;
import io.github.bothuany.security.jwt.dto.LoginRequest;
import io.github.bothuany.security.jwt.dto.TokenResponse;
import io.github.bothuany.security.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller for the user-service.
 * This is the only auth controller in the system.
 * Other services should use Feign clients to communicate with this controller.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authenticationService.authenticate(loginRequest);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/login-with-email")
    public ResponseEntity<JwtResponseDTO> loginWithEmail(@Valid @RequestBody UserLoginDTO loginDTO) {
        JwtResponseDTO response = userService.login(loginDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponseDTO> register(@Valid @RequestBody UserRegisterDTO registerRequest) {
        JwtResponseDTO response = userService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
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
}