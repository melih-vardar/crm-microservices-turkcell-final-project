package com.turkcell.crmmicroserviceshw4.userservice.controller;

import com.turkcell.crmmicroserviceshw4.userservice.service.UserService;
import io.github.bothuany.dtos.user.JwtResponseDTO;
import io.github.bothuany.dtos.user.UserLoginDTO;
import io.github.bothuany.dtos.user.UserRegisterDTO;
import io.github.bothuany.dtos.user.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for user management operations")
public class UserController {

    @Autowired
    private UserService userService;

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
}