package com.turkcell.crmmicroservicesfinalproject.userservice.service.impl;

import io.github.bothuany.dtos.user.Role;
import com.turkcell.crmmicroservicesfinalproject.userservice.model.User;
import com.turkcell.crmmicroservicesfinalproject.userservice.repository.UserRepository;
import com.turkcell.crmmicroservicesfinalproject.userservice.rules.UserBusinessRules;
import com.turkcell.crmmicroservicesfinalproject.userservice.security.JwtTokenProvider;
import com.turkcell.crmmicroservicesfinalproject.userservice.service.UserService;
import io.github.bothuany.dtos.user.JwtResponseDTO;
import io.github.bothuany.dtos.user.UserLoginDTO;
import io.github.bothuany.dtos.user.UserRegisterDTO;
import io.github.bothuany.dtos.user.UserResponseDTO;
import io.github.bothuany.event.analytics.CreateUserAnalyticsEvent;
import io.github.bothuany.event.analytics.LoginUserAnalyticsEvent;
import io.github.bothuany.event.notification.EmailNotificationEvent;
import io.github.bothuany.event.notification.PushNotificationEvent;
import io.github.bothuany.event.notification.SmsNotificationEvent;
import io.github.bothuany.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private final StreamBridge streamBridge;
    private final UserBusinessRules businessRules;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public JwtResponseDTO register(UserRegisterDTO registerDTO) {
        // İş kurallarını kontrol et
        businessRules.checkIfUsernameExists(registerDTO.getUsername());
        businessRules.checkIfEmailExists(registerDTO.getEmail());

        // Create new user
        User user = User.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role(registerDTO.getRole() != null ? registerDTO.getRole() : Role.CUSTOMER_REPRESENTATIVE)
                .build();

        userRepository.save(user);
        /*
         * sendEmailNotification(user,
         * "Welcome to Our Service! 🎉",
         * "Dear " + user.getUsername() + ",\n\n"
         * +
         * "We are thrilled to welcome you to turkcell! Thank you for registering with us.\n\n"
         * + "Here are a few things you can do next:\n"
         * + "✅ Explore your account and personalize your settings.\n"
         * + "✅ Check out our latest features and services.\n"
         * + "✅ Contact our support team if you need any assistance.\n\n"
         * + "We're excited to have you on board!\n\n"
         * + "Best regards,\n[turkcell] Support Team"
         * );
         */
        // Authenticate user and generate token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        registerDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateJwtTokenWithRoles(authentication, user.getId());

        UserResponseDTO userResponseDTO = mapToUserResponseDTO(user);

        // Extract roles from token to include in response
        List<String> roles = jwtTokenProvider.getRolesFromJwtToken(jwt);

        sendUserRegistryAnalytics(user);
        return JwtResponseDTO.builder()
                .token(jwt)
                .user(userResponseDTO)
                .roles(roles)
                .build();
    }

    @Override
    public JwtResponseDTO login(UserLoginDTO loginDTO) {
        // E-posta adresine ait kullanıcının var olup olmadığını kontrol et
        businessRules.checkIfUserExistsByEmail(loginDTO.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new BusinessException("User not found with email: " + loginDTO.getEmail()));

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        loginDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Include user ID and roles in token
        String jwt = jwtTokenProvider.generateJwtTokenWithRoles(authentication, user.getId());

        // Create response
        UserResponseDTO userResponseDTO = mapToUserResponseDTO(user);
        /*
         * sendEmailNotification(user,
         * "Security Alert: A Login to Your Account Was Detected",
         * "Dear " + user.getUsername() + ",\n\n"
         * +
         * "We detected a successful login to your account. If this was you, no further action is required.\n\n"
         * +
         * "If you did not perform this login, please change your password immediately and contact our support team.\n\n"
         * + "Best regards,\n[Turkcell] Support Team"
         * );
         */
        sendUserLoginAnalytics(user);

        // Extract roles from token to include in response
        List<String> roles = jwtTokenProvider.getRolesFromJwtToken(jwt);

        return JwtResponseDTO.builder()
                .token(jwt)
                .user(userResponseDTO)
                .roles(roles)
                .build();
    }

    @Override
    public UserResponseDTO getUserById(UUID id) {
        // Kullanıcının var olup olmadığını kontrol et
        businessRules.checkIfUserExists(id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));

        return mapToUserResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUser(UUID id, UserRegisterDTO userDTO) {
        // Kullanıcının var olup olmadığını kontrol et
        businessRules.checkIfUserExists(id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));

        // Kullanıcı adının ve e-posta adresinin benzersiz olup olmadığını kontrol et
        businessRules.checkIfUsernameExistsForUpdate(userDTO.getUsername(), user.getUsername());
        businessRules.checkIfEmailExistsForUpdate(userDTO.getEmail(), user.getEmail());

        // Update user
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        // Only update password if it's provided
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        userRepository.save(user);

        return mapToUserResponseDTO(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        // Kullanıcının var olup olmadığını kontrol et
        businessRules.checkIfUserExists(id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));

        // Kullanıcının zaten silinip silinmediğini kontrol et
        if (user.isDeleted()) {
            throw new BusinessException("User is already deleted: " + id);
        }

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDTO> getAllDeletedUsers() {
        return userRepository.findAllByDeletedAtIsNotNull().stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDTO> getAllUsersIncludingDeleted() {
        return userRepository.findAllIncludingDeleted().stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponseDTO restoreUser(UUID id) {
        // We need to use the findAllIncludingDeleted query to bypass the @Where clause
        User user = userRepository.findAllIncludingDeleted().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));

        // Kullanıcının silinmiş olup olmadığını kontrol et
        businessRules.checkIfUserIsDeleted(user);

        // Restore the user by setting deletedAt to null
        user.setDeletedAt(null);
        userRepository.save(user);

        return mapToUserResponseDTO(user);
    }

    @Override
    public UserResponseDTO getCurrentUser(String token) {
        // Token'ın geçerli olup olmadığını kontrol et
        businessRules.checkIfTokenIsValid(token);

        if (!jwtTokenProvider.validateJwtToken(token)) {
            throw new BusinessException("Invalid or expired token");
        }

        String username = jwtTokenProvider.getUserNameFromJwtToken(token);

        // Kullanıcının var olup olmadığını kontrol et
        businessRules.checkIfUserExistsByUsername(username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found with username: " + username));

        return mapToUserResponseDTO(user);
    }

    @Override
    public boolean logout(String token) {
        // Token'ın geçerli olup olmadığını kontrol et
        businessRules.checkIfTokenIsValid(token);

        // Token'ı blacklist'e ekleyerek geçersiz kıl
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Token'ı geçersiz kıl
        boolean invalidated = jwtTokenProvider.invalidateJwtToken(token);

        if (invalidated) {
            logger.info("User logged out successfully, token invalidated");
        } else {
            logger.warn("Failed to invalidate token during logout");
        }

        return invalidated;
    }

    @Override
    public Role getUserRole(UUID id) {
        // Kullanıcının var olup olmadığını kontrol et
        businessRules.checkIfUserExists(id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));

        return user.getRole();
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole());
    }

    private void sendEmailNotification(User user, String subject, String message) {
        try {
            EmailNotificationEvent emailEvent = new EmailNotificationEvent();
            emailEvent.setEmail(user.getEmail());
            emailEvent.setSubject(subject);
            emailEvent.setMessage(message);
            streamBridge.send("emailNotification-out-0", emailEvent);
            logger.info("Email notification sent to: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send email notification: {}", e.getMessage());
        }
    }

    private void sendSmsNotification(User user, String message) {
        try {
            SmsNotificationEvent smsEvent = new SmsNotificationEvent();
            smsEvent.setPhoneNumber("+1234567890"); // Replace with actual phone number
            smsEvent.setMessage(message);
            streamBridge.send("smsNotification-out-0", smsEvent);
            logger.info("SMS notification sent");
        } catch (Exception e) {
            logger.error("Failed to send SMS notification: {}", e.getMessage());
        }
    }

    private void sendPushNotification(User user, String title, String message) {
        try {
            PushNotificationEvent pushEvent = new PushNotificationEvent();
            pushEvent.setUserId(user.getId());
            pushEvent.setTitle(title);
            pushEvent.setMessage(message);
            streamBridge.send("pushNotification-out-0", pushEvent);
            logger.info("Push notification sent to user: {}", user.getUsername());
        } catch (Exception e) {
            logger.error("Failed to send push notification: {}", e.getMessage());
        }
    }

    private void sendUserRegistryAnalytics(User user) {
        try {
            CreateUserAnalyticsEvent createUserAnalyticsEvent = new CreateUserAnalyticsEvent();
            createUserAnalyticsEvent.setUsername(user.getUsername());
            createUserAnalyticsEvent.setEmail(user.getEmail());
            createUserAnalyticsEvent.setUserid(user.getId());
            createUserAnalyticsEvent.setEventType("USER_REGISTER");
            createUserAnalyticsEvent.setDateTime(LocalDateTime.now());
            streamBridge.send("CreateUserAnalytics-out-0", createUserAnalyticsEvent);
            logger.info("Created user analytics event: {}", createUserAnalyticsEvent.getEventType());
        } catch (Exception e) {
            // Log error but allow application to continue
            logger.error("Error while sending user registration analytics: {}", e.getMessage(), e);
        }
    }

    private void sendUserLoginAnalytics(User user) {
        try {
            logger.info("sendUserLoginAnalytics called for user: {}", user.getUsername());
            LoginUserAnalyticsEvent loginUserAnalyticsEvent = new LoginUserAnalyticsEvent();
            loginUserAnalyticsEvent.setUserId(String.valueOf(user.getId()));
            loginUserAnalyticsEvent.setEmail(user.getEmail());
            loginUserAnalyticsEvent.setEventType("USER_LOGIN");
            loginUserAnalyticsEvent.setLoginStartTime(System.currentTimeMillis());

            boolean result = streamBridge.send("LoginUserAnalytics-out-0", loginUserAnalyticsEvent);
            logger.info("Event sent: {}, Send result: {}", loginUserAnalyticsEvent.getEventType(), result);
        } catch (Exception e) {
            // Log error but allow application to continue
            logger.error("Error while sending user login analytics: {}", e.getMessage(), e);
        }
    }
}