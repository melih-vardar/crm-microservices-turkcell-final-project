package com.turkcell.crmmicroserviceshw4.userservice.service;

import io.github.bothuany.dtos.user.JwtResponseDTO;
import io.github.bothuany.dtos.user.Role;
import io.github.bothuany.dtos.user.UserLoginDTO;
import io.github.bothuany.dtos.user.UserRegisterDTO;
import io.github.bothuany.dtos.user.UserResponseDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    JwtResponseDTO register(UserRegisterDTO registerDTO);

    JwtResponseDTO login(UserLoginDTO loginDTO);

    UserResponseDTO getUserById(UUID id);

    UserResponseDTO updateUser(UUID id, UserRegisterDTO userDTO);

    void deleteUser(UUID id);

    List<UserResponseDTO> getAllUsers();

    // New methods for managing deleted users
    List<UserResponseDTO> getAllDeletedUsers();

    List<UserResponseDTO> getAllUsersIncludingDeleted();

    UserResponseDTO restoreUser(UUID id);

    // New methods for the missing endpoints
    UserResponseDTO getCurrentUser(String token);

    boolean logout(String token);

    Role getUserRole(UUID id);
}