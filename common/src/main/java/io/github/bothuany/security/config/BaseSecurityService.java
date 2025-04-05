package io.github.bothuany.security.config;

import io.github.bothuany.security.filter.BaseJwtAuthFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BaseSecurityService {
    private static final String[] WHITE_LIST_URLS = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/api/v1/auth/**",
            "/api/auth/**"
    };

    private final BaseJwtAuthFilter baseJwtAuthFilter;

    public BaseSecurityService(BaseJwtAuthFilter baseJwtAuthFilter) {
        this.baseJwtAuthFilter = baseJwtAuthFilter;
    }

    public HttpSecurity configureCoreSecurity(HttpSecurity httpSecurity) throws Exception {
        log.debug("Configuring core security with whitelisted URLs: {}", String.join(", ", WHITE_LIST_URLS));

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req.requestMatchers(WHITE_LIST_URLS).permitAll())
                .addFilterBefore(baseJwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        log.debug("Core security configuration completed");
        return httpSecurity;
    }
}
