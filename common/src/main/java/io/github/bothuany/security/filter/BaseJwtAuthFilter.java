package io.github.bothuany.security.filter;

import io.github.bothuany.security.jwt.service.BaseJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class BaseJwtAuthFilter extends OncePerRequestFilter {
    private final BaseJwtService jwtService;

    public BaseJwtAuthFilter(BaseJwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String requestURI = request.getRequestURI();
        String jwtHeader = request.getHeader("Authorization");

        log.debug("Request URI: {}", requestURI);
        log.debug("Authorization header present: {}", jwtHeader != null);

        if (jwtHeader != null && jwtHeader.startsWith("Bearer ")) {
            String jwt = jwtHeader.substring(7);

            try {
                String username = jwtService.extractUsername(jwt);
                log.debug("Extracted username from token: {}", username);

                List<String> roles = jwtService.extractRoles(jwt);
                if (roles == null || roles.isEmpty()) {
                    log.debug("No roles found in token, using default USER role");
                } else {
                    log.debug("Roles from token: {}", roles);
                }

                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                if (jwtService.validateToken(jwt)) {
                    log.debug("Token is valid, setting authentication for {}", username);

                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            username, null, authorities);
                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(token);
                } else {
                    log.warn("Invalid JWT token for user: {}", username);
                }
            } catch (Exception e) {
                log.error("Error processing JWT token: {}", e.getMessage());
                // Continue without setting authentication
            }
        } else {
            log.debug("No JWT token found in request");
        }

        filterChain.doFilter(request, response);
    }
}
