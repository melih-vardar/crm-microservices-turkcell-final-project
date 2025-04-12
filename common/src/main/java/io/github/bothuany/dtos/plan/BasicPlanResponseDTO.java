package io.github.bothuany.dtos.plan;

import io.github.bothuany.enums.PlanType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicPlanResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private PlanType planType;
}