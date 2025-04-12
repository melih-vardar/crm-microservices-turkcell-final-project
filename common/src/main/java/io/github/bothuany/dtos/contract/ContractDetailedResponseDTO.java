package io.github.bothuany.dtos.contract;

import io.github.bothuany.dtos.customer.CustomerResponseDTO;
import io.github.bothuany.dtos.plan.PlanResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractDetailedResponseDTO {
    private UUID id;
    private CustomerResponseDTO customer;
    private PlanResponseDTO plan;
    private String startDate;
    private String endDate;
    private boolean isActive;
}