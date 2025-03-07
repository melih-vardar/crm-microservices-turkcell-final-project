package com.turkcell.crmmicroserviceshw4.gatewayserver.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Global CORS configuration
                .route("cors_route", r -> r.path("/**")
                        .filters(f -> f.filter((exchange, chain) -> {
                            exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "*");
                            exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods",
                                    "GET, POST, PUT, DELETE, OPTIONS");
                            exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers",
                                    "Content-Type, Authorization");
                            return chain.filter(exchange);
                        }))
                        .uri("no://op"))
                .build();
    }
}