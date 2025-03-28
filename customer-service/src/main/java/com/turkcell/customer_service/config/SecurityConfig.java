package com.turkcell.customer_service.config;

import io.github.bothuany.security.config.BaseSecurityConfig;
import io.github.bothuany.security.jwt.JwtProperties;
import io.github.bothuany.security.jwt.JwtService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableMethodSecurity
public class SecurityConfig extends BaseSecurityConfig {
    public SecurityConfig(JwtService jwtService, JwtProperties jwtProperties, UserDetailsService userDetailsService) {
        super(jwtService, jwtProperties, userDetailsService);
    }
}