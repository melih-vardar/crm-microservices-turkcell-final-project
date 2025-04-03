package com.turkcell.analyticsservice.service;

import com.turkcell.analyticsservice.dto.ForUserDto.CreateExampleDto;
import com.turkcell.analyticsservice.dto.ForUserDto.LoginExampleDto;
import io.github.bothuany.event.analytics.CreateUserAnalyticsEvent;
import io.github.bothuany.event.analytics.LoginUserAnalyticsEvent;

public interface UserBehaviorService {

    public void registerAnalyticsToUser(CreateUserAnalyticsEvent createUserAnalyticsEvent);
    public void sendUserLoginAnalytics(LoginUserAnalyticsEvent loginUserAnalyticsEvent);
}
