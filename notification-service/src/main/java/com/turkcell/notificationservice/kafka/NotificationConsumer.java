package com.turkcell.notificationservice.kafka;

import com.turkcell.notificationservice.service.NotificationService;
import io.github.bothuany.dtos.notification.EmailNotificationDTO;
import io.github.bothuany.dtos.notification.PushNotificationDTO;
import io.github.bothuany.dtos.notification.SmsNotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "email-notifications", groupId = "notification-group")
    public void consumerEmailNotification(EmailNotificationDTO emailNotificationDTO) {
        notificationService.sendEmail(emailNotificationDTO);
    }

    @KafkaListener(topics = "sms-notifications", groupId = "notification-group")
    public void consumerSmsNotification(SmsNotificationDTO smsNotificationDTO) {
        notificationService.sendSms(smsNotificationDTO);
    }

    @KafkaListener(topics = "push-notifications", groupId = "notification-group")
    public void consumerPushNotification(PushNotificationDTO pushNotificationDTO) {
        notificationService.sendPushNotification(pushNotificationDTO);
    }


}
