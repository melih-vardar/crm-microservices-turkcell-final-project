package com.turkcell.analyticsservice.controller;

import com.turkcell.analyticsservice.dto.dto.GetAllLoginUserDto;
import com.turkcell.analyticsservice.dto.dto.GetAllUserAnalyticsDto;
import com.turkcell.analyticsservice.dto.dto.GetUserBehaviorDto;
import com.turkcell.analyticsservice.service.UserBehaviorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics/user-behavior")
@RequiredArgsConstructor
public class UserBehaviorController {

    private final UserBehaviorService userBehaviorService;

    @GetMapping("/user-create-analytics")
    public ResponseEntity<List<GetAllUserAnalyticsDto>> getAllCreateUserAnalytics() {
        return ResponseEntity.ok(userBehaviorService.getAllCreateUserAnalytics());
    }
    @GetMapping("/user-analytics/by-email")
    public ResponseEntity<GetUserBehaviorDto> getCreateUserBehaviorAnalytic(@RequestParam String email) {
        return ResponseEntity.ok(userBehaviorService.getCreateUserBehaviorAnalytic(email));
    }
    @GetMapping("/login-analytics")
    public ResponseEntity<List<GetAllLoginUserDto>> getAllLoginUserAnalytics() {
        return ResponseEntity.ok(userBehaviorService.GetAllLoginUserAnalytics());
    }


}
