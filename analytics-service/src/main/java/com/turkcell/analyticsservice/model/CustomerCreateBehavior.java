package com.turkcell.analyticsservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer_create_behavior")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerCreateBehavior {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID customerId;
    private String username;
    private String email;
    private EventType eventType;
    private LocalDateTime dateTime;
}
