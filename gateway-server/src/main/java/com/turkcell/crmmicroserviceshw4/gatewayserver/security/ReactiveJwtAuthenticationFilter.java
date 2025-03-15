package com.turkcell.crmmicroserviceshw4.gatewayserver.security;

import com.turkcell.crmmicroserviceshw4.gatewayserver.client.UserDetailsServiceClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReactiveJwtAuthenticationFilter implements WebFilter {
    private static final Logger logger = LoggerFactory.getLogger(ReactiveJwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final UserDetailsServiceClient userDetailsServiceClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = extractTokenFromRequest(exchange.getRequest());

        if (StringUtils.hasText(token)) {
            // Make sure we're only passing the actual token part to the JwtService
            if (token.toLowerCase().startsWith("bearer ")) {
                token = token.substring(7);
            }

            if (jwtService.validateToken(token)) {
                String username = jwtService.getUsernameFromToken(token);

                return Mono.fromCallable(() -> userDetailsServiceClient.loadUserByUsername(username).getBody())
                        .flatMap(userDetails -> {
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                            return chain.filter(exchange)
                                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                        })
                        .onErrorResume(e -> {
                            logger.error("Cannot set user authentication: {}", e.getMessage());
                            return chain.filter(exchange);
                        });
            }
        }

        return chain.filter(exchange);
    }

    private String extractTokenFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}