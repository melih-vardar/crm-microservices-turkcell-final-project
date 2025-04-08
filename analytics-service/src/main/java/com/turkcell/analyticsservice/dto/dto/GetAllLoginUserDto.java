package com.turkcell.analyticsservice.dto.dto;

import com.turkcell.analyticsservice.model.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetAllLoginUserDto {

    private UUID id;
    private String email;
    private long loginStartTime; // Epoch millis
    private long durationMs;
    private EventType EventType;
    private LocalDateTime eventTime;
}
