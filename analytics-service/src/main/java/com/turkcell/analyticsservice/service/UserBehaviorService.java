package com.turkcell.analyticsservice.service;

import com.turkcell.analyticsservice.dto.ForUserDto.CreateExampleDto;
import com.turkcell.analyticsservice.dto.ForUserDto.LoginExampleDto;

public interface UserBehaviorService {

    public void registerAnalyticsToUser(CreateExampleDto exampleDto);
    public void loginAnalyticsToUser(LoginExampleDto exampleDto);
}
