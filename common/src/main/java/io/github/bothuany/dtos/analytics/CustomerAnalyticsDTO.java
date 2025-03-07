package io.github.bothuany.dtos.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAnalyticsDTO {
    private int totalCustomers;
    private int activeContracts;
    private int expiredContracts;
}