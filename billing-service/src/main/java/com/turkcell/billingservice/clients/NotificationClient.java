package com.turkcell.billingservice.clients;

import com.turkcell.billingservice.dtos.EmailNotificationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", path = "/api/v1/notifications")
public interface NotificationClient {
    @PostMapping("/email")
    void sendEmailNotification(@RequestBody EmailNotificationDTO notification);
} 