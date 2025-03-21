package com.turkcell.analyticsservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_behavior")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserBehavior {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;
    private String email;

    @Enumerated(EnumType.STRING)
    private EventType EventType;
    private LocalDateTime dateTime;
}
