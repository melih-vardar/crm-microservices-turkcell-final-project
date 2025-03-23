package com.turkcell.analyticsservice.dto.ForUserDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateExampleCustomerDto {
    private UUID customerId;
    private String firstname;
    private String lastname;
    private String email;
    private String eventType;
    private LocalDateTime eventTime;

}
