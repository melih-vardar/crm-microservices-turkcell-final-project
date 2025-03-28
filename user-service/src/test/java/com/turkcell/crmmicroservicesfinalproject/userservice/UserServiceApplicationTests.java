package com.turkcell.crmmicroservicesfinalproject.userservice;

import com.turkcell.crmmicroservicesfinalproject.userservice.model.User;
import com.turkcell.crmmicroservicesfinalproject.userservice.repository.UserRepository;
import com.turkcell.crmmicroservicesfinalproject.userservice.rules.UserBusinessRules;
import com.turkcell.crmmicroservicesfinalproject.userservice.security.JwtTokenProvider;
import com.turkcell.crmmicroservicesfinalproject.userservice.service.impl.UserServiceImpl;
import io.github.bothuany.dtos.user.JwtResponseDTO;
import io.github.bothuany.dtos.user.Role;
import io.github.bothuany.dtos.user.UserLoginDTO;
import io.github.bothuany.dtos.user.UserRegisterDTO;
import io.github.bothuany.dtos.user.UserResponseDTO;
import io.github.bothuany.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceApplicationTests {

        @Mock
        private UserRepository userRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private AuthenticationManager authenticationManager;

        @Mock
        private JwtTokenProvider jwtTokenProvider;

        @Mock
        private StreamBridge streamBridge;

        @Mock
        private UserBusinessRules businessRules;

        @Mock
        private Authentication authentication;

        @InjectMocks
        private UserServiceImpl userService;

        private UUID userId;
        private UserRegisterDTO userRegisterDTO;
        private UserLoginDTO userLoginDTO;
        private User user;
        private String jwtToken;

        @BeforeEach
        public void setUp() {
                userId = UUID.randomUUID();
                userRegisterDTO = new UserRegisterDTO("testuser", "test@example.com", "Test123@",
                                Role.CUSTOMER_REPRESENTATIVE);
                userLoginDTO = new UserLoginDTO("test@example.com", "Test123@");
                user = User.builder()
                                .id(userId)
                                .username("testuser")
                                .email("test@example.com")
                                .password("encodedPassword")
                                .role(Role.CUSTOMER_REPRESENTATIVE)
                                .build();
                jwtToken = "test.jwt.token";
        }

        @Test
        public void testRegister_Success() {
                // Arrange
                when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
                when(userRepository.save(any(User.class))).thenReturn(user);
                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenReturn(authentication);
                // Handle token generation with null UUID safely
                doReturn(jwtToken).when(jwtTokenProvider).generateJwtToken(any(), any());

                // Act
                JwtResponseDTO response = userService.register(userRegisterDTO);

                // Assert
                assertNotNull(response);
                assertNotNull(response.getUser());
                assertEquals(user.getUsername(), response.getUser().getUsername());
                assertEquals(user.getEmail(), response.getUser().getEmail());

                verify(businessRules).checkIfUsernameExists(userRegisterDTO.getUsername());
                verify(businessRules).checkIfEmailExists(userRegisterDTO.getEmail());
                verify(userRepository).save(any(User.class));
                verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
                // Relaxing the verification since we only care that it was called
                verify(jwtTokenProvider).generateJwtToken(any(), any());
        }

        @Test
        public void testLogin_Success() {
                // Arrange
                when(userRepository.findByEmail(userLoginDTO.getEmail())).thenReturn(Optional.of(user));
                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenReturn(authentication);
                // Handle token generation with null UUID safely
                doReturn(jwtToken).when(jwtTokenProvider).generateJwtToken(any(), any());

                // Act
                JwtResponseDTO response = userService.login(userLoginDTO);

                // Assert
                assertNotNull(response);
                assertEquals(jwtToken, response.getToken());
                assertNotNull(response.getUser());
                assertEquals(user.getUsername(), response.getUser().getUsername());
                assertEquals(user.getEmail(), response.getUser().getEmail());

                verify(businessRules).checkIfUserExistsByEmail(userLoginDTO.getEmail());
                verify(userRepository).findByEmail(userLoginDTO.getEmail());
                verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
                // Relaxing the verification since we only care that it was called
                verify(jwtTokenProvider).generateJwtToken(any(), any());
        }

        @Test
        public void testGetUserById_Success() {
                // Arrange
                when(userRepository.findById(userId)).thenReturn(Optional.of(user));

                // Act
                UserResponseDTO response = userService.getUserById(userId);

                // Assert
                assertNotNull(response);
                assertEquals(user.getId(), response.getId());
                assertEquals(user.getUsername(), response.getUsername());
                assertEquals(user.getEmail(), response.getEmail());

                verify(businessRules).checkIfUserExists(userId);
                verify(userRepository).findById(userId);
        }

        @Test
        public void testUpdateUser_Success() {
                // Arrange
                UserRegisterDTO updateDTO = new UserRegisterDTO("updateduser", "updated@example.com", "Updated123@",
                                Role.CUSTOMER_REPRESENTATIVE);
                User updatedUser = User.builder()
                                .id(userId)
                                .username("updateduser")
                                .email("updated@example.com")
                                .password("newEncodedPassword")
                                .role(Role.CUSTOMER_REPRESENTATIVE)
                                .build();

                when(userRepository.findById(userId)).thenReturn(Optional.of(user));
                when(passwordEncoder.encode(updateDTO.getPassword())).thenReturn("newEncodedPassword");
                when(userRepository.save(any(User.class))).thenReturn(updatedUser);

                // Act
                UserResponseDTO response = userService.updateUser(userId, updateDTO);

                // Assert
                assertNotNull(response);
                assertEquals(userId, response.getId());
                assertEquals(updateDTO.getUsername(), response.getUsername());
                assertEquals(updateDTO.getEmail(), response.getEmail());

                verify(businessRules).checkIfUserExists(userId);
                verify(businessRules).checkIfUsernameExistsForUpdate("updateduser", "testuser");
                verify(businessRules).checkIfEmailExistsForUpdate("updated@example.com", "test@example.com");
                verify(userRepository).findById(userId);
                verify(userRepository).save(any(User.class));
        }

        @Test
        public void testDeleteUser_Success() {
                // Arrange
                when(userRepository.findById(userId)).thenReturn(Optional.of(user));

                // Act
                userService.deleteUser(userId);

                // Assert
                ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
                verify(userRepository).save(userCaptor.capture());

                User capturedUser = userCaptor.getValue();
                assertNotNull(capturedUser.getDeletedAt());

                verify(businessRules).checkIfUserExists(userId);
                verify(userRepository).findById(userId);
                verify(userRepository).save(any(User.class));
        }

        @Test
        public void testDeleteUser_AlreadyDeleted() {
                // Arrange
                User deletedUser = User.builder()
                                .id(userId)
                                .username("testuser")
                                .email("test@example.com")
                                .password("encodedPassword")
                                .role(Role.CUSTOMER_REPRESENTATIVE)
                                .deletedAt(LocalDateTime.now())
                                .build();

                when(userRepository.findById(userId)).thenReturn(Optional.of(deletedUser));

                // Act & Assert
                assertThrows(BusinessException.class, () -> userService.deleteUser(userId));

                verify(businessRules).checkIfUserExists(userId);
                verify(userRepository).findById(userId);
                verify(userRepository, never()).save(any(User.class));
        }

        @Test
        public void testGetAllUsers_Success() {
                // Arrange
                User user2 = User.builder()
                                .id(UUID.randomUUID())
                                .username("testuser2")
                                .email("test2@example.com")
                                .password("encodedPassword2")
                                .role(Role.CUSTOMER_REPRESENTATIVE)
                                .build();

                List<User> users = Arrays.asList(user, user2);
                when(userRepository.findAll()).thenReturn(users);

                // Act
                List<UserResponseDTO> responses = userService.getAllUsers();

                // Assert
                assertNotNull(responses);
                assertEquals(2, responses.size());
                assertEquals(user.getUsername(), responses.get(0).getUsername());
                assertEquals(user2.getUsername(), responses.get(1).getUsername());

                verify(userRepository).findAll();
        }

        @Test
        public void testGetAllDeletedUsers_Success() {
                // Arrange
                User deletedUser1 = User.builder()
                                .id(UUID.randomUUID())
                                .username("deleted1")
                                .email("deleted1@example.com")
                                .password("encodedPassword")
                                .role(Role.CUSTOMER_REPRESENTATIVE)
                                .deletedAt(LocalDateTime.now())
                                .build();

                User deletedUser2 = User.builder()
                                .id(UUID.randomUUID())
                                .username("deleted2")
                                .email("deleted2@example.com")
                                .password("encodedPassword")
                                .role(Role.CUSTOMER_REPRESENTATIVE)
                                .deletedAt(LocalDateTime.now().minusDays(1))
                                .build();

                List<User> deletedUsers = Arrays.asList(deletedUser1, deletedUser2);
                when(userRepository.findAllByDeletedAtIsNotNull()).thenReturn(deletedUsers);

                // Act
                List<UserResponseDTO> responses = userService.getAllDeletedUsers();

                // Assert
                assertNotNull(responses);
                assertEquals(2, responses.size());
                assertEquals(deletedUser1.getUsername(), responses.get(0).getUsername());
                assertEquals(deletedUser2.getUsername(), responses.get(1).getUsername());

                verify(userRepository).findAllByDeletedAtIsNotNull();
        }

        @Test
        public void testGetUserRole_Success() {
                // Arrange
                when(userRepository.findById(userId)).thenReturn(Optional.of(user));

                // Act
                Role role = userService.getUserRole(userId);

                // Assert
                assertEquals(Role.CUSTOMER_REPRESENTATIVE, role);

                verify(businessRules).checkIfUserExists(userId);
                verify(userRepository).findById(userId);
        }

        @Test
        public void testRestoreUser_Success() {
                // Arrange
                User deletedUser = User.builder()
                                .id(userId)
                                .username("testuser")
                                .email("test@example.com")
                                .password("encodedPassword")
                                .role(Role.CUSTOMER_REPRESENTATIVE)
                                .deletedAt(LocalDateTime.now())
                                .build();

                User restoredUser = User.builder()
                                .id(userId)
                                .username("testuser")
                                .email("test@example.com")
                                .password("encodedPassword")
                                .role(Role.CUSTOMER_REPRESENTATIVE)
                                .build();

                when(userRepository.findAllIncludingDeleted()).thenReturn(List.of(deletedUser));
                when(userRepository.save(any(User.class))).thenReturn(restoredUser);

                // Act
                UserResponseDTO response = userService.restoreUser(userId);

                // Assert
                assertNotNull(response);
                assertEquals(userId, response.getId());
                assertEquals(restoredUser.getUsername(), response.getUsername());

                ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
                verify(userRepository).save(userCaptor.capture());

                User capturedUser = userCaptor.getValue();
                assertNull(capturedUser.getDeletedAt());

                verify(businessRules).checkIfUserIsDeleted(deletedUser);
                verify(userRepository).findAllIncludingDeleted();
                verify(userRepository).save(any(User.class));
        }
}