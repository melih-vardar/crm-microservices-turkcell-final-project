package com.turkcell.crmmicroserviceshw4.gatewayserver.client;

import com.turkcell.crmmicroserviceshw4.gatewayserver.model.dto.UserDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/api/users")
public interface UserDetailsServiceClient {

    @GetMapping("/details/{username}")
    ResponseEntity<UserDetailsDTO> loadUserByUsername(@PathVariable String username);
}