package com.turkcell.plan_service.entity;

import io.github.bothuany.security.encryption.Encryptable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

    @Encryptable
    private String name;

    @Encryptable
    private String description;

    @Encryptable
    private double price;

    @Encryptable
    private int durationInMonths;
}
