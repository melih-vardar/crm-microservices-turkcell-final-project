package com.turkcell.crmmicroserviceshw4.userservice.controller;

import com.turkcell.crmmicroserviceshw4.userservice.service.UserService;
import io.github.bothuany.dtos.user.JwtResponseDTO;
import io.github.bothuany.dtos.user.Role;
import io.github.bothuany.dtos.user.UserLoginDTO;
import io.github.bothuany.dtos.user.UserRegisterDTO;
import io.github.bothuany.dtos.user.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import com.turkcell.crmmicroserviceshw4.userservice.model.dto.UserDetailsDTO;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for user management operations")
@AllArgsConstructor
public class UserController {
    private UserService userService;
    private UserDetailsService userDetailsService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns JWT token")
    public ResponseEntity<JwtResponseDTO> registerUser(@Valid @RequestBody UserRegisterDTO registerDTO) {
        return new ResponseEntity<>(userService.register(registerDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates a user and returns JWT token")
    public ResponseEntity<JwtResponseDTO> loginUser(@Valid @RequestBody UserLoginDTO loginDTO) {
        return ResponseEntity.ok(userService.login(loginDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves user information by ID")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates user information")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable UUID id,
            @Valid @RequestBody UserRegisterDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves information for all users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by ID")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test")
    public String test() {
        return "User Service is working!";
    }

    @GetMapping("/deleted")
    @Operation(summary = "Get all deleted users", description = "Retrieves information for all deleted users")
    public ResponseEntity<List<UserResponseDTO>> getAllDeletedUsers() {
        return ResponseEntity.ok(userService.getAllDeletedUsers());
    }

    @GetMapping("/all")
    @Operation(summary = "Get all users including deleted", description = "Retrieves information for all users including deleted ones")
    public ResponseEntity<List<UserResponseDTO>> getAllUsersIncludingDeleted() {
        return ResponseEntity.ok(userService.getAllUsersIncludingDeleted());
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "Restore deleted user", description = "Restores a previously deleted user")
    public ResponseEntity<UserResponseDTO> restoreUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.restoreUser(id));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user information", description = "Retrieves information for the currently authenticated user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserResponseDTO> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        // Extract token from Authorization header (Bearer token)
        String token = authHeader.substring(7);
        return ResponseEntity.ok(userService.getCurrentUser(token));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Invalidates the JWT token", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        // Extract token from Authorization header (Bearer token)
        String token = authHeader.substring(7);
        boolean success = userService.logout(token);

        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/roles")
    @Operation(summary = "Get user roles", description = "Retrieves roles for a specific user (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Role> getUserRoles(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserRole(id));
    }

    @GetMapping("/details/{username}")
    @Operation(summary = "Get user details by username", description = "Retrieves user details for authentication")
    public ResponseEntity<UserDetailsDTO> getUserDetailsByUsername(@PathVariable String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return ResponseEntity.ok(new UserDetailsDTO(userDetails));
    }
}