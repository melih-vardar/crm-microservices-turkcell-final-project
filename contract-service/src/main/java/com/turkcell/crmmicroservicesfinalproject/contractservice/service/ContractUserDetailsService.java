package com.turkcell.crmmicroservicesfinalproject.contractservice.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ContractUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // In a microservice architecture, Contract Service does not authenticate users directly
        // This is just a stub implementation for token validation
        
        try {
            // Return a placeholder user just for token validation
            return new User(
                    username,
                    "", // password is not needed for token validation
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
}
