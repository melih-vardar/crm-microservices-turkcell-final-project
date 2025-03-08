package com.turkcell.notificationservice.kafka;

import com.turkcell.notificationservice.service.NotificationService;
import io.github.bothuany.dtos.notification.EmailNotificationDTO;
import io.github.bothuany.dtos.notification.PushNotificationDTO;
import io.github.bothuany.dtos.notification.SmsNotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    // Email notification function
    @Bean
    public Consumer<EmailNotificationDTO> emailNotificationFunction() {
        return notificationService::sendEmail;
    }

    // SMS notification function
    @Bean
    public Consumer<SmsNotificationDTO> smsNotificationFunction() {
        return notificationService::sendSms;
    }

    // Push notification function
    @Bean
    public Consumer<PushNotificationDTO> pushNotificationFunction() {
        return notificationService::sendPushNotification;
    }


}
