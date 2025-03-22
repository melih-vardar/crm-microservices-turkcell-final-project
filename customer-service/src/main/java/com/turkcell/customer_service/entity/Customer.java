package com.turkcell.customer_service.entity;

import io.github.bothuany.security.encryption.AttributeEncryptor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @UuidGenerator
    @Column(name = "id")
    private UUID id;

    @Convert(converter = AttributeEncryptor.class)
    @Column(name = "firstname")
    private String firstName;

    @Convert(converter = AttributeEncryptor.class)
    @Column(name = "lastname")
    private String lastName;

    @Convert(converter = AttributeEncryptor.class)
    @Column(name = "email")
    private String email;

    @Convert(converter = AttributeEncryptor.class)
    @Column(name = "phone")
    private String phone;

    @Convert(converter = AttributeEncryptor.class)
    @Column(name = "address")
    private String address;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
