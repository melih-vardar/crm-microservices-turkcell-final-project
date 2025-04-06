package com.turkcell.analyticsservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "bill_behavior")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BillBehavior {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    String customerId;
    BigDecimal amount;
    LocalDateTime dueDate;
    private LocalDateTime createdAt;
}
