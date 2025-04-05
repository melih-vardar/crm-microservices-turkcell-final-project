package com.turkcell.crmmicroservicesfinalproject.userservice.config;

import io.github.bothuany.security.config.BaseSecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final BaseSecurityService baseSecurityService;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/validate",
            "/api/auth/test",
            "/api/v1/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security for user-service");

        http = baseSecurityService.configureCoreSecurity(http);

        // Configure security rules
        http.authorizeHttpRequests(auth -> {
            log.info("Setting up authorization rules");
            auth.requestMatchers(PUBLIC_ENDPOINTS).permitAll();
            log.info("Public endpoints configured: {}", String.join(", ", PUBLIC_ENDPOINTS));
            auth.requestMatchers("/api/users/**").authenticated();
            log.info("Protected user endpoints configured");
            auth.anyRequest().authenticated();
            log.info("Default rule: all other requests require authentication");
        });

        log.info("Security configuration completed for user-service");
        return http.build();
    }

    /**
     * Feign client için JWT token'ı ekleyen interceptor
     * Bu, servisler arası iletişimde JWT token'ı otomatik olarak ekler
     */
    @Bean
    public RequestInterceptor feignClientInterceptor() {
        log.info("Configuring Feign client interceptor");
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authorizationHeader = request.getHeader("Authorization");
                if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
                    log.info("Adding Authorization header to Feign request");
                    requestTemplate.header("Authorization", authorizationHeader);
                } else {
                    log.info("No Authorization header found in request for Feign client");
                }
            }
        };
    }
}
