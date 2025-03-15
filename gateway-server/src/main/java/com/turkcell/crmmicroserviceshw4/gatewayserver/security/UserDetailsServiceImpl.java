package com.turkcell.crmmicroserviceshw4.gatewayserver.security;

import com.turkcell.crmmicroserviceshw4.gatewayserver.client.UserDetailsServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDetailsServiceClient userDetailsServiceClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return userDetailsServiceClient.loadUserByUsername(username).getBody();
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found with username: " + username, e);
        }
    }
}