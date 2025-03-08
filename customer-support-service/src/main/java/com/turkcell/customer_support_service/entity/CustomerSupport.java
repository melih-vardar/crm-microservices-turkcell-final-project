package com.turkcell.customer_support_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Document(collection = "supportTickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSupport {
    @Id
    @UuidGenerator
    private UUID id;

    private UUID customerId;

    private String issueType;

    private String description;

    private String status;
}
