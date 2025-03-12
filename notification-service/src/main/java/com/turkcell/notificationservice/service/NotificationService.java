package com.turkcell.notificationservice.service;


import io.github.bothuany.event.notification.EmailNotificationEvent;
import io.github.bothuany.event.notification.PushNotificationEvent;
import io.github.bothuany.event.notification.SmsNotificationEvent;

public interface NotificationService {

    public void sendEmail(EmailNotificationEvent emailNotificationEvent);

    public void sendSms(SmsNotificationEvent smsNotificationEvent);

    public void sendPushNotification(PushNotificationEvent pushNotificationEvent);

}
