package com.turkcell.analyticsservice.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TicketAnalyticsDto {
    private UUID id;
    UUID ticketId;
    UUID customerId;
    String eventType;
    String status;
    LocalDateTime eventTime;
}
