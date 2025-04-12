package com.turkcell.customer_support_service.client;

import io.github.bothuany.dtos.user.JwtResponseDTO;
import io.github.bothuany.dtos.user.UserLoginDTO;
import io.github.bothuany.dtos.user.UserRegisterDTO;
import io.github.bothuany.security.jwt.dto.TokenResponse;
import io.github.bothuany.security.jwt.dto.TokenValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "userservice")
public interface UserServiceClient {

    @GetMapping("/api/users/role/{id}")
    String getUserRole(@PathVariable("id") UUID id);

    // Auth endpoints
    @PostMapping("/api/auth/login")
    ResponseEntity<TokenResponse> login(@RequestBody UserLoginDTO loginDTO);

    @PostMapping("/api/auth/register")
    ResponseEntity<JwtResponseDTO> register(@RequestBody UserRegisterDTO registerDTO);

    @PostMapping("/api/auth/logout")
    ResponseEntity<?> logout(@RequestHeader("Authorization") String token);

    /**
     * Test endpoint to verify connectivity
     */
    @GetMapping("/api/auth/test")
    ResponseEntity<Map<String, String>> testAuth();

    /**
     * Validates a JWT token with the user-service
     * 
     * @param token JWT token to validate (with Bearer prefix)
     * @return Response containing validation result and user info if valid
     */
    @GetMapping("/api/auth/validate")
    ResponseEntity<TokenValidationResponse> validateToken(@RequestHeader("Authorization") String token);
}