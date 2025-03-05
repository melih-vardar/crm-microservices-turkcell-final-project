package io.github.bothuany.dtos.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreateDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
}