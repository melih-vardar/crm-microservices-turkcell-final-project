package io.github.bothuany.dtos.contract;

import io.github.bothuany.enums.PlanType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponseDTO {
    private UUID id;
    private UUID customerId;
    private PlanType planType;
    private String startDate;
    private String endDate;
    private boolean isActive;
}