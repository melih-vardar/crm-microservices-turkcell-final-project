package com.turkcell.analyticsservice.dto.ForUserDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginExampleDto {
    String userId;
    String email;
    long loginStartTime;
    String eventType;
}