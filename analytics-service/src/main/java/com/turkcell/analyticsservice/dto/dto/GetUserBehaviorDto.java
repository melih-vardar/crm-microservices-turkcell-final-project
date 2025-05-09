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
public class GetUserBehaviorDto {
    private UUID id;
    private String username;
    private String email;
    private String eventType;
    private LocalDateTime dateTime;
}
