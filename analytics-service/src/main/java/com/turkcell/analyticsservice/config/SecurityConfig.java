package com.turkcell.analyticsservice.config;

import feign.RequestInterceptor;
import io.github.bothuany.security.config.BaseSecurityService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableMethodSecurity
public class SecurityConfig {
    private final BaseSecurityService baseSecurityService;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api/contracts/test" // Test endpoint is public
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security for contract-service");

        // First apply base security configuration
        baseSecurityService.configureCoreSecurity(http);

        // Then add specific configuration for this service
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                .anyRequest().authenticated());

        log.info("Security configuration completed for contract-service");

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
                    log.info("Adding Authorization header to Feign request: {}",
                            authorizationHeader.substring(0, Math.min(15, authorizationHeader.length())) + "...");
                    requestTemplate.header("Authorization", authorizationHeader);
                } else {
                    log.info("No Authorization header found in the request for Feign client");
                }
            }
        };
    }
}