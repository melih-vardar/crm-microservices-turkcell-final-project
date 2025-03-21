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

    private UUID userid;
    private String username;
    private String email;
    private String eventType;
    private LocalDateTime dateTime;


}
