package com.turkcell.analyticsservice.service.impl;

import com.turkcell.analyticsservice.dto.ForUserDto.CreateExampleDto;
import com.turkcell.analyticsservice.kafka.AnalyticsPipeline;
import com.turkcell.analyticsservice.model.EventType;
import com.turkcell.analyticsservice.model.UserBehavior;
import com.turkcell.analyticsservice.repository.UserBehaviorRepository;
import com.turkcell.analyticsservice.service.UserBehaviorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserBehaviorServiceServiceImpl implements UserBehaviorService {

    private final UserMetrics userMetrics;
    private final UserBehaviorRepository userBehaviorRepository;
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsPipeline.class);

    public void registerAnalyticsToUser(CreateExampleDto exampleDto){
        logger.info("Registering analytics to user start ");
        UserBehavior userBehavior = new UserBehavior();
        userBehavior.setUsername(exampleDto.getUsername());
        userBehavior.setEmail(exampleDto.getEmail());
        userBehavior.setDateTime(LocalDateTime.now());
        userBehavior.setEventType(EventType.valueOf(exampleDto.getEventType()));
        logger.info("converted userBehavior {}", userBehavior);

        userBehaviorRepository.save(userBehavior);

        userMetrics.incrementUserRegistrations();
            //Spring Actuator otomatik olarak /actuator/prometheus endpoint'inde metrikleri sunar:
            //user_registrations_total{application="analytics-service"} 1500
            //Grafana'da Görselleştirme

    }

}
