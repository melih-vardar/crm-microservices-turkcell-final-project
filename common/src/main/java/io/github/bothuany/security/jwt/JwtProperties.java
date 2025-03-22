package io.github.bothuany.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretKey = "default-very-secure-jwt-secret-key-used-for-signing-tokens";
    private long expirationMs = 86400000; // 24 hours
    private String issuer = "microservice-auth";
    private String tokenPrefix = "Bearer ";
    private String headerString = "Authorization";
}