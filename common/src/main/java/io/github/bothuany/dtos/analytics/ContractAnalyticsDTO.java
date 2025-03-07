package io.github.bothuany.dtos.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractAnalyticsDTO {
    private String planType;
    private int activeContracts;
    private int newContractsThisMonth;
}