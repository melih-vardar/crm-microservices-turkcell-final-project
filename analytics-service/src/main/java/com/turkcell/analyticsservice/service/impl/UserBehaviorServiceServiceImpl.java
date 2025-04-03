package com.turkcell.analyticsservice.service.impl;


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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserBehaviorServiceServiceImpl implements UserBehaviorService {

    private final UserMetrics userMetrics;
    private final UserCreateBehaviorRepository userBehaviorRepository;
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsPipeline.class);

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
            //Spring Actuator otomatik olarak /actuator/prometheus endpoint'inde metrikleri sunar:
            //user_registrations_total{application="analytics-service"} 1500
            //Grafana'da Görselleştirme
    }

    @Override
    public void sendUserLoginAnalytics(LoginUserAnalyticsEvent loginUserAnalyticsEvent) {
        UserLoginBehavior userLoginBehavior = new UserLoginBehavior();
        userLoginBehavior.setId(UUID.fromString(loginUserAnalyticsEvent.getUserId()));
        userLoginBehavior.setEmail(loginUserAnalyticsEvent.getEmail());
        userLoginBehavior.setEventType(EventType.valueOf(loginUserAnalyticsEvent.getEventType()));
        userLoginBehavior.setLoginStartTime(loginUserAnalyticsEvent.getLoginStartTime());

        userMetrics.incrementLoginAttempts();

        long duration = System.currentTimeMillis() - userLoginBehavior.getLoginStartTime();
        userMetrics.recordLoginDuration(duration);

    }
}
