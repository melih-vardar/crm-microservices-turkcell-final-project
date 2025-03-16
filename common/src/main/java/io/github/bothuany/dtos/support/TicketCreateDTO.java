package io.github.bothuany.dtos.support;

import io.github.bothuany.enums.IssueTypes;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreateDTO {
    @NotNull(message = "Customer ID cannot be null")
    private UUID customerId;

    @NotBlank(message = "Issue type cannot be empty")
    @Pattern(regexp = "^(TECHNICAL|BILLING|SERVICE|OTHER)$", message = "Issue type must be one of: TECHNICAL, BILLING, SERVICE, OTHER")
    private IssueTypes issueType;

    @NotBlank(message = "Description cannot be empty")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;
}