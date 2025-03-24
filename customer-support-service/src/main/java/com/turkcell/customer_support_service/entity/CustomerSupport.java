package com.turkcell.customer_support_service.entity;

import io.github.bothuany.enums.IssueTypes;
import io.github.bothuany.enums.TicketStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "supportTickets")
@Data
public class CustomerSupport {
    @Id
    private UUID id;

    private UUID customerId;

    private IssueTypes issueType;

    @Convert(converter = AttributeEncryptor.class)
    private String description;

    private TicketStatus status;

    public CustomerSupport(IssueTypes issueType, UUID customerId,String description, TicketStatus status) {
        this.id = UUID.randomUUID(); // Generate UUID for id
        this.customerId = customerId;
        this.issueType = issueType;
        this.description = description;
        this.status = status;
    }

    public CustomerSupport() {
        this.id = UUID.randomUUID();
    }
}
