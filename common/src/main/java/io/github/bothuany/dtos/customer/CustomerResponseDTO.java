package io.github.bothuany.dtos.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}