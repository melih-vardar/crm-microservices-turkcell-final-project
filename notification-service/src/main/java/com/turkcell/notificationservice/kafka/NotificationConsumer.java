package com.turkcell.notificationservice.kafka;

import com.turkcell.notificationservice.service.NotificationService;
import com.turkcell.notificationservice.service.NotificationServiceImpl;
import io.github.bothuany.dtos.notification.EmailNotificationDTO;
import io.github.bothuany.dtos.notification.PushNotificationDTO;
import io.github.bothuany.dtos.notification.SmsNotificationDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    // Email notification function
    @Bean
    public Consumer<EmailNotificationDTO> emailNotificationFunction() {
        // return notificationService::sendEmail;
        return emailNotificationDTO -> {
            try {
                logger.info("Received email notification: {}", emailNotificationDTO);
                notificationService.sendEmail(emailNotificationDTO);
            } catch (Exception e) {
                logger.error("Failed to process email notification: {}", emailNotificationDTO, e);
            }
        };
    }

    // SMS notification function
    @Bean
    public Consumer<SmsNotificationDTO> smsNotificationFunction() {
        return notificationService::sendSms;
    }

    // Push notification function
    @Bean
    public Consumer<PushNotificationDTO> pushNotificationFunction() {
        return pushNotificationDTO -> {
            // Log ekledik
            logger.info("Received push notification request: User ID: {} | Title: {} | Message: {}",
                    pushNotificationDTO.getUserId(),
                    pushNotificationDTO.getTitle(),
                    pushNotificationDTO.getMessage()
            );

            // Bildirimi gönder
            notificationService.sendPushNotification(pushNotificationDTO);
        };
    }


}