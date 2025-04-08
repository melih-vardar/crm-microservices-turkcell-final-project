package com.turkcell.analyticsservice.dto.dto;

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
public class CustomerAnalyticDto {
     UUID id;
     String customerId;
     String firstName;
     String lastName;
     String email;
     String eventType;
     LocalDateTime dateTime;

}
