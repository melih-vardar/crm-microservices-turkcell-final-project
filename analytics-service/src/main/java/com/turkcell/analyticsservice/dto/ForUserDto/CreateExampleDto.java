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
public class CreateExampleDto {
     UUID id;
     String username;
     String email;
     String eventType;
     LocalDateTime dateTime;


}
