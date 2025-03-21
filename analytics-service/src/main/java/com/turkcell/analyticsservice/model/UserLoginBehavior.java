package com.turkcell.analyticsservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_login_behavior")
@Builder
public class UserLoginBehavior {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String email;
    @Column(name = "login_start_time")
    private long loginStartTime; // Epoch millis
    @Column(name = "duration_ms")
    private long durationMs;
    @Enumerated(EnumType.STRING)
    private EventType EventType;
    private LocalDateTime eventTime;
}
