package com.turkcell.analyticsservice.service;

import com.turkcell.analyticsservice.dto.dto.GetAllLoginUserDto;
import com.turkcell.analyticsservice.dto.dto.GetAllUserAnalyticsDto;
import com.turkcell.analyticsservice.dto.dto.GetUserBehaviorDto;
import io.github.bothuany.event.analytics.CreateUserAnalyticsEvent;
import io.github.bothuany.event.analytics.LoginUserAnalyticsEvent;

import java.util.List;

public interface UserBehaviorService {


    void registerAnalyticsToUser(CreateUserAnalyticsEvent createUserAnalyticsEvent);
    void sendUserLoginAnalytics(LoginUserAnalyticsEvent loginUserAnalyticsEvent);
    List<GetAllUserAnalyticsDto> getAllCreateUserAnalytics();
    public GetUserBehaviorDto getCreateUserBehaviorAnalytic(String email);
    public List<GetAllLoginUserDto> GetAllLoginUserAnalytics();



}
