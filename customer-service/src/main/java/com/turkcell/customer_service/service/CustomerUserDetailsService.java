package com.turkcell.customer_service.service;

import com.turkcell.customer_service.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    private final UserServiceClient userServiceClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // In a microservice architecture, Customer Service may validate tokens
        // but doesn't need to know user credentials. This is just a stub
        // implementation.
        // In real implementation, you might want to call user-service via Feign client

        try {
            // This is a stub implementation and would use the Feign client to get user
            // details
            // from the User Service in a complete implementation

            // For now, we return a placeholder user just for token validation
            return new User(
                    username,
                    "", // password is not needed for token validation
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
}