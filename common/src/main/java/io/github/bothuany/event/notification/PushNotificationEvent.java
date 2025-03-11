package io.github.bothuany.event.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PushNotificationEvent {
    private UUID userId;
    private String title;
    private String message;
}
