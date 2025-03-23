package com.turkcell.analyticsservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "subscription_analytics")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubscriptionAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID subscriptionId;
    private String planType;
    private String changeType;
    private LocalDate dateTime;
}
