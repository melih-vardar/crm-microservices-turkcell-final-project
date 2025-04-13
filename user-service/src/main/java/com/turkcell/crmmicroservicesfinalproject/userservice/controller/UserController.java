package com.turkcell.crmmicroservicesfinalproject.userservice.controller;

import com.turkcell.crmmicroservicesfinalproject.userservice.service.UserService;
import io.github.bothuany.dtos.user.Role;
import io.github.bothuany.dtos.user.UserRegisterDTO;
import io.github.bothuany.dtos.user.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for user management operations")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityExpression.hasUserId(#id)")
    @Operation(summary = "Get user by ID", description = "Retrieves user information by ID")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityExpression.hasUserId(#id)")
    @Operation(summary = "Update user", description = "Updates user information")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable UUID id,
            @Valid @RequestBody UserRegisterDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieves information for all users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Deletes a user by ID")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test")
    public String test() {
        return "User Service is working!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test/admin")
    public String testAdmin() {
        return "Admin endpoint is working!";
    }

    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE')")
    @GetMapping("/test/rep")
    public String testRep() {
        return "Customer Representative endpoint is working!";
    }

    @GetMapping("/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all deleted users", description = "Retrieves information for all deleted users")
    public ResponseEntity<List<UserResponseDTO>> getAllDeletedUsers() {
        return ResponseEntity.ok(userService.getAllDeletedUsers());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users including deleted", description = "Retrieves information for all users including deleted ones")
    public ResponseEntity<List<UserResponseDTO>> getAllUsersIncludingDeleted() {
        return ResponseEntity.ok(userService.getAllUsersIncludingDeleted());
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Restore deleted user", description = "Restores a previously deleted user")
    public ResponseEntity<UserResponseDTO> restoreUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.restoreUser(id));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user information", description = "Retrieves information for the currently authenticated user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserResponseDTO> getCurrentUser(@RequestHeader("Authorization") String token) {
        // Remove "Bearer " prefix from the token
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ResponseEntity.ok(userService.getCurrentUser(token));
    }

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user roles", description = "Retrieves roles for a specific user (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Role> getUserRoles(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserRole(id));
    }
}