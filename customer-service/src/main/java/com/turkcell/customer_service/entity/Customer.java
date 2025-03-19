package com.turkcell.customer_service.entity;

import io.github.bothuany.security.encryption.Encryptable;
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

    @Encryptable
    @Column(name = "firstname")
    private String firstName;

    @Encryptable
    @Column(name = "lastname")
    private String lastName;

    @Encryptable
    @Column(name = "email")
    private String email;

    @Encryptable
    @Column(name = "phone")
    private String phone;

    @Encryptable
    @Column(name = "address")
    private String address;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
