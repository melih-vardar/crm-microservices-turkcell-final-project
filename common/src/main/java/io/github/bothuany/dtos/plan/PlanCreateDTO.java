package io.github.bothuany.dtos.plan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanCreateDTO {
    private String name;
    private String description;
    private double price;
    private int durationInMonths;
}