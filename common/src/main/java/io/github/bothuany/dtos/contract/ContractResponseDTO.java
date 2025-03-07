package io.github.bothuany.dtos.contract;

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
    private String planType;
    private String startDate;
    private String endDate;
    private boolean isActive;
}