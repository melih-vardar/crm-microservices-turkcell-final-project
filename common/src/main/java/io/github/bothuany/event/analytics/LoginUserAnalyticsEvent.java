package io.github.bothuany.event.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserAnalyticsEvent {
    String userId;
    String email;
    long loginStartTime;
    String eventType;
}
