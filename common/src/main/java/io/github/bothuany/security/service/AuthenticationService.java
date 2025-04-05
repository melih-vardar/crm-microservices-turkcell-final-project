package io.github.bothuany.security.service;

import io.github.bothuany.security.jwt.dto.LoginRequest;
import io.github.bothuany.security.jwt.dto.TokenResponse;
import io.github.bothuany.security.jwt.service.BaseJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for handling authentication operations.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final BaseJwtService jwtService;

    /**
     * Authenticate a user with username and password
     * 
     * @param loginRequest containing username and password
     * @return TokenResponse with JWT token if authentication is successful
     */
    public TokenResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Generate token with roles
        String jwt = jwtService.generateToken(userDetails.getUsername(), null, roles);

        return TokenResponse.builder()
                .accessToken(jwt)
                .username(userDetails.getUsername())
                .roles(roles)
                .build();
    }
}