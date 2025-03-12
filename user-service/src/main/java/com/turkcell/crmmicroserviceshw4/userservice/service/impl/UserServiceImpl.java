package com.turkcell.crmmicroserviceshw4.userservice.service.impl;

import io.github.bothuany.dtos.user.Role;
import com.turkcell.crmmicroserviceshw4.userservice.model.User;
import com.turkcell.crmmicroserviceshw4.userservice.repository.UserRepository;
import com.turkcell.crmmicroserviceshw4.userservice.security.JwtUtils;
import com.turkcell.crmmicroserviceshw4.userservice.service.UserService;
import io.github.bothuany.dtos.user.JwtResponseDTO;
import io.github.bothuany.dtos.user.UserLoginDTO;
import io.github.bothuany.dtos.user.UserRegisterDTO;
import io.github.bothuany.dtos.user.UserResponseDTO;
import io.github.bothuany.event.notification.EmailNotificationEvent;
import io.github.bothuany.event.notification.PushNotificationEvent;
import io.github.bothuany.event.notification.SmsNotificationEvent;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    private final StreamBridge streamBridge; // Sanırım Autowired ile çalışmıyor
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Override
    public JwtResponseDTO register(UserRegisterDTO registerDTO) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Create new user
        User user = User.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role(registerDTO.getRole() != null ? registerDTO.getRole() : Role.CUSTOMER_REPRESENTATIVE)
                .build();

        userRepository.save(user);
        /*sendEmailNotification(user,
                "Welcome to Our Service! 🎉",
                "Dear " + user.getUsername() + ",\n\n"
                        + "We are thrilled to welcome you to turkcell! Thank you for registering with us.\n\n"
                        + "Here are a few things you can do next:\n"
                        + "✅ Explore your account and personalize your settings.\n"
                        + "✅ Check out our latest features and services.\n"
                        + "✅ Contact our support team if you need any assistance.\n\n"
                        + "We’re excited to have you on board!\n\n"
                        + "Best regards,\n[turkcell] Support Team"
        );*/

        // Authenticate user and generate token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        registerDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Create response
        UserResponseDTO userResponseDTO = mapToUserResponseDTO(user);

        return JwtResponseDTO.builder()
                .token(jwt)
                .user(userResponseDTO)
                .build();
    }

    @Override
    public JwtResponseDTO login(UserLoginDTO loginDTO) {
        // Find user by email
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + loginDTO.getEmail()));

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        loginDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Create response
        UserResponseDTO userResponseDTO = mapToUserResponseDTO(user);
        /*sendEmailNotification(user,
                "Security Alert: A Login to Your Account Was Detected",
                "Dear " + user.getUsername() + ",\n\n"
                        + "We detected a successful login to your account. If this was you, no further action is required.\n\n"
                        + "If you did not perform this login, please change your password immediately and contact our support team.\n\n"
                        + "Best regards,\n[Turkcell] Support Team"
        );*/
        return JwtResponseDTO.builder()
                .token(jwt)
                .user(userResponseDTO)
                .build();
    }

    @Override
    public UserResponseDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        return mapToUserResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUser(UUID id, UserRegisterDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        // Check if username is taken by another user
        if (!user.getUsername().equals(userDTO.getUsername()) &&
                userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        // Check if email is taken by another user
        if (!user.getEmail().equals(userDTO.getEmail()) &&
                userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

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
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole());
    }

    private void sendEmailNotification(User user,String subject,String message) {
        EmailNotificationEvent emailNotificationEvent = new EmailNotificationEvent();
        emailNotificationEvent.setEmail(user.getEmail());
        emailNotificationEvent.setSubject(subject);
        emailNotificationEvent.setMessage(message);
        logger.info("Sending email notification: {}", emailNotificationEvent);
        streamBridge.send("emailNotification-out-0", emailNotificationEvent);
    }
    //burada sms gönderilmesine gerek yok modelde phone yok
    private void sendSmsNotification(User user,String message) {
        SmsNotificationEvent smsNotificationEvent = new SmsNotificationEvent();
        //smsNotificationEvent.setPhoneNumber(user.getPhone());
        smsNotificationEvent.setMessage(message);
        logger.info("Sending SMS notification: {}", smsNotificationEvent);
        streamBridge.send("smsNotification-out-0", smsNotificationEvent);
    }

    private void sendPushNotification(User user,String title,String message) {
        PushNotificationEvent pushNotificationEvent = new PushNotificationEvent();
        pushNotificationEvent.setUserId(user.getId());
        pushNotificationEvent.setTitle(title);
        //pushNotificationDTO.setDeviceToken("EXAMPLE_DEVICE_TOKEN"); // Gerçek token buraya gelmeli
        pushNotificationEvent.setMessage(message);
        logger.info("Sending Push Notification to user: {} | Title: {} | Message: {}",
                pushNotificationEvent.getUserId(), pushNotificationEvent.getTitle(), pushNotificationEvent.getMessage());
        streamBridge.send("pushNotification-out-0", pushNotificationEvent);
    }
}