package io.github.bothuany.dtos.plan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponseDTO {
    private UUID id;
    private BasicPlanResponseDTO basicPlan;
    private double price;
    private int durationInMonths;
}