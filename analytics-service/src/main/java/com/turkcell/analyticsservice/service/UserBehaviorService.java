package com.turkcell.analyticsservice.service;

import com.turkcell.analyticsservice.dto.ForUserDto.CreateExampleDto;

public interface UserBehaviorService {

    public void registerAnalyticsToUser(CreateExampleDto exampleDto);
}
