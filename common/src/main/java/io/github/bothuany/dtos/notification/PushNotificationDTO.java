package io.github.bothuany.dtos.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationDTO {
    private UUID userId;
    private String title;
    private String message;
}