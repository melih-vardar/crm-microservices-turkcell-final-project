package io.github.bothuany.dtos.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreateDTO {
    private UUID customerId;
    private String issueType;
    private String description;
}