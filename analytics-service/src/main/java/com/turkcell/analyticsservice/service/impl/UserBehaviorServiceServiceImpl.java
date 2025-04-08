package com.turkcell.analyticsservice.service.impl;


import com.turkcell.analyticsservice.dto.dto.GetAllLoginUserDto;
import com.turkcell.analyticsservice.dto.dto.GetAllUserAnalyticsDto;
import com.turkcell.analyticsservice.dto.dto.GetUserBehaviorDto;
import com.turkcell.analyticsservice.kafka.AnalyticsPipeline;
import com.turkcell.analyticsservice.model.EventType;
import com.turkcell.analyticsservice.model.UserCreateBehavior;
import com.turkcell.analyticsservice.model.UserLoginBehavior;
import com.turkcell.analyticsservice.repository.UserCreateBehaviorRepository;
import com.turkcell.analyticsservice.repository.UserLoginBehaviorRepository;
import com.turkcell.analyticsservice.service.UserBehaviorService;
import io.github.bothuany.event.analytics.CreateUserAnalyticsEvent;
import io.github.bothuany.event.analytics.LoginUserAnalyticsEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserBehaviorServiceServiceImpl implements UserBehaviorService {

    private final UserMetrics userMetrics;
    private final UserCreateBehaviorRepository userBehaviorRepository;
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsPipeline.class);
    private final UserLoginBehaviorRepository userLoginBehaviorRepository;

    public void registerAnalyticsToUser(CreateUserAnalyticsEvent createUserAnalyticsEvent){
        logger.info("Registering analytics to user start ");
        UserCreateBehavior userBehavior = new UserCreateBehavior();
        userBehavior.setUsername(createUserAnalyticsEvent.getUsername());
        userBehavior.setEmail(createUserAnalyticsEvent.getEmail());
        userBehavior.setDateTime(LocalDateTime.now());
        userBehavior.setEventType(EventType.valueOf(createUserAnalyticsEvent.getEventType()));
        logger.info("converted userBehavior {}", userBehavior);
        userBehaviorRepository.save(userBehavior);
        userMetrics.incrementUserRegistrations();
    }

    @Override
    public void sendUserLoginAnalytics(LoginUserAnalyticsEvent loginUserAnalyticsEvent) {
        UserLoginBehavior userLoginBehavior = new UserLoginBehavior();
        userLoginBehavior.setId(UUID.fromString(loginUserAnalyticsEvent.getUserId()));
        userLoginBehavior.setEmail(loginUserAnalyticsEvent.getEmail());
        userLoginBehavior.setEventType(EventType.valueOf(loginUserAnalyticsEvent.getEventType()));
        userLoginBehavior.setLoginStartTime(loginUserAnalyticsEvent.getLoginStartTime());
        userLoginBehaviorRepository.save(userLoginBehavior);
        userMetrics.incrementLoginAttempts();
        long duration = System.currentTimeMillis() - userLoginBehavior.getLoginStartTime();
        userMetrics.recordLoginDuration(duration);
    }

    public List<GetAllUserAnalyticsDto> getAllCreateUserAnalytics(){
        List<UserCreateBehavior> userCreateBehaviors= userBehaviorRepository.findAll();

        return userCreateBehaviors.stream().map(userBehavior -> new GetAllUserAnalyticsDto(
                userBehavior.getId(),
                userBehavior.getUsername(),
                userBehavior.getEmail(),
                userBehavior.getEventType().name(),
                userBehavior.getDateTime()
        )).collect(Collectors.toList());
    }
    public GetUserBehaviorDto getCreateUserBehaviorAnalytic(String email){
        UserCreateBehavior userCreateBehavior = userBehaviorRepository.findByEmail(email);
        GetUserBehaviorDto getUserBehaviorDto = new GetUserBehaviorDto();
        getUserBehaviorDto.setId(userCreateBehavior.getId());
        getUserBehaviorDto.setUsername(userCreateBehavior.getUsername());
        getUserBehaviorDto.setEmail(userCreateBehavior.getEmail());
        getUserBehaviorDto.setEventType(userCreateBehavior.getEventType().name());
        getUserBehaviorDto.setDateTime(userCreateBehavior.getDateTime());
        return getUserBehaviorDto;
    }

    public List<GetAllLoginUserDto> GetAllLoginUserAnalytics(){

        List<UserLoginBehavior> userLoginBehaviors = userLoginBehaviorRepository.findAll();
        return userLoginBehaviors.stream().map(userLoginBehavior -> new GetAllLoginUserDto(
                userLoginBehavior.getId(),
                userLoginBehavior.getEmail(),
                userLoginBehavior.getLoginStartTime(),
                userLoginBehavior.getDurationMs(),
                userLoginBehavior.getEventType(),
                userLoginBehavior.getEventTime()
        )).collect(Collectors.toList());
    }

}
