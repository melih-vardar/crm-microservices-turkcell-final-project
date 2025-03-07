package com.turkcell.notificationservice.service;

import io.github.bothuany.dtos.notification.EmailNotificationDTO;
import io.github.bothuany.dtos.notification.PushNotificationDTO;
import io.github.bothuany.dtos.notification.SmsNotificationDTO;

public interface NotificationService {

    public void sendEmail(EmailNotificationDTO emailNotificationDTO);

    public void sendSms(SmsNotificationDTO smsNotificationDTO);

    public void sendPushNotification(PushNotificationDTO pushNotificationDTO);

}
