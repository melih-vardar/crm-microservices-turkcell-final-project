package com.turkcell.analyticsservice.model;

import io.github.bothuany.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer_support")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CustomerSupportBehavior {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID ticketId;
    private UUID customerId;
    private EventType eventType;
    private TicketStatus ticketStatus;
    private LocalDateTime eventTime;

}
