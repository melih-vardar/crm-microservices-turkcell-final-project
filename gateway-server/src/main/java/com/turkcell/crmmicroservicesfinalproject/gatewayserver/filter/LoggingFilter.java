package com.turkcell.crmmicroservicesfinalproject.gatewayserver.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Log route information if available
        Set<String> routeIds = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR) != null
                ? Collections
                        .singleton(((Route) exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR)).getId())
                : Collections.emptySet();

        // Log headers
        String headers = request.getHeaders().entrySet().stream()
                .map(entry -> entry.getKey() + ": " + String.join(", ", entry.getValue()))
                .collect(Collectors.joining(", "));

        // Log full request details
        LOGGER.info("Request: {} {} (Route: {})\nHeaders: {}",
                request.getMethod(),
                request.getURI(),
                String.join(", ", routeIds),
                headers);

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    // Log response with more details
                    LOGGER.info("Response: {} (Route: {})",
                            exchange.getResponse().getStatusCode(),
                            String.join(", ", routeIds));
                }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}