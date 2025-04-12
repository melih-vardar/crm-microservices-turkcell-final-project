package com.turkcell.plan_service.entity;

import io.github.bothuany.enums.PlanType;
import io.github.bothuany.security.encryption.AttributeEncryptor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity(name = "basic_plans")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicPlan {
    @Id
    @UuidGenerator
    private UUID id;

    @Convert(converter = AttributeEncryptor.class)
    private String name;

    @Convert(converter = AttributeEncryptor.class)
    private String description;

    @Enumerated(EnumType.STRING)
    @Convert(converter = AttributeEncryptor.class)
    private PlanType planType;
}