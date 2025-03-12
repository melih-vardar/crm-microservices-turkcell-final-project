package com.turkcell.notificationservice.kafka;

import com.turkcell.notificationservice.service.NotificationService;
import com.turkcell.notificationservice.service.NotificationServiceImpl;

import io.github.bothuany.event.notification.EmailNotificationEvent;
import io.github.bothuany.event.notification.PushNotificationEvent;
import io.github.bothuany.event.notification.SmsNotificationEvent;
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
    public Consumer<EmailNotificationEvent> emailNotificationFunction() {
        // return notificationService::sendEmail;
        return emailNotificationEvent -> {
            try {
                logger.info("Received email notification: {}", emailNotificationEvent);
                notificationService.sendEmail(emailNotificationEvent);
            } catch (Exception e) {
                logger.error("Failed to process email notification: {}", emailNotificationEvent, e);
            }
        };
    }

    // SMS notification function
    @Bean
    public Consumer<SmsNotificationEvent> smsNotificationFunction() {
        return notificationService::sendSms;
    }

    // Push notification function
    @Bean
    public Consumer<PushNotificationEvent> pushNotificationFunction() {
        return pushNotificationEvent -> {
            // Log ekledik
            logger.info("Received push notification request: User ID: {} | Title: {} | Message: {}",
                    pushNotificationEvent.getUserId(),
                    pushNotificationEvent.getTitle(),
                    pushNotificationEvent.getMessage()
            );

            // Bildirimi gönder
            notificationService.sendPushNotification(pushNotificationEvent);
        };
    }


}