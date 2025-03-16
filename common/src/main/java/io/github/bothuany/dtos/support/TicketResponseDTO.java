package io.github.bothuany.dtos.support;

import io.github.bothuany.enums.IssueTypes;
import io.github.bothuany.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponseDTO {
    private UUID id;
    private UUID customerId;
    private IssueTypes issueType;
    private TicketStatus status;
}