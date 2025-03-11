package io.github.bothuany.event.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailNotificationEvent {
    private String email;
    private String subject;
    private String message;
}
