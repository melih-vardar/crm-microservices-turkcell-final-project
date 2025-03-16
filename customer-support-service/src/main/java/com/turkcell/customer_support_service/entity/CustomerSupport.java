package com.turkcell.customer_support_service.entity;

import io.github.bothuany.enums.IssueTypes;
import io.github.bothuany.enums.TicketStatus;
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

    private IssueTypes issueType;

    private String description;

    private TicketStatus status;
}
