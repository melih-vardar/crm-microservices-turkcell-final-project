package com.turkcell.plan_service.entity;

import io.github.bothuany.security.encryption.AttributeEncryptor;
import jakarta.persistence.Convert;
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

    @Convert(converter = AttributeEncryptor.class)
    private String name;

    @Convert(converter = AttributeEncryptor.class)
    private String description;

    @Convert(converter = AttributeEncryptor.class)
    private double price;

    @Convert(converter = AttributeEncryptor.class)
    private int durationInMonths;
}
