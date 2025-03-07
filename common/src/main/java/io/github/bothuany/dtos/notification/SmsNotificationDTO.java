package io.github.bothuany.dtos.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsNotificationDTO {
    private String phoneNumber;
    private String message;
}