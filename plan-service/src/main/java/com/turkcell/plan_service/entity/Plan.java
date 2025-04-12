package com.turkcell.plan_service.entity;

import io.github.bothuany.security.encryption.AttributeEncryptor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity(name = "plans")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Plan {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "basic_plan_id")
    private BasicPlan basicPlan;

    private double price;

    private int durationInMonths;
}