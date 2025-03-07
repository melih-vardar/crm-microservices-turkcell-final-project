package io.github.bothuany.dtos.billing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillCreateDTO {
    private UUID customerId;
    private double amount;
    private String dueDate;
}