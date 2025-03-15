package com.turkcell.crmmicroserviceshw4.gatewayserver.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "utility-server", path = "/api/jwt")
public interface JwtServiceClient {

    @PostMapping("/generate")
    ResponseEntity<String> generateToken(@RequestParam String username);

    @GetMapping("/validate")
    ResponseEntity<Boolean> validateToken(@RequestParam String token);

    @GetMapping("/username")
    ResponseEntity<String> getUsernameFromToken(@RequestParam String token);
}