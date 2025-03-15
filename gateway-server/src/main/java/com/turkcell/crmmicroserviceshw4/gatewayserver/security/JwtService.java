package com.turkcell.crmmicroserviceshw4.gatewayserver.security;

import com.turkcell.crmmicroserviceshw4.gatewayserver.client.JwtServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtServiceClient jwtServiceClient;

    public String generateToken(String username) {
        return jwtServiceClient.generateToken(username).getBody();
    }

    public boolean validateToken(String token) {
        if (token != null && token.toLowerCase().startsWith("bearer ")) {
            token = token.substring(7);
        }

        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        return Boolean.TRUE.equals(jwtServiceClient.validateToken(encodedToken).getBody());
    }

    public String getUsernameFromToken(String token) {
        if (token != null && token.toLowerCase().startsWith("bearer ")) {
            token = token.substring(7);
        }

        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        return jwtServiceClient.getUsernameFromToken(encodedToken).getBody();
    }
}