package com.turkcell.analyticsservice.service.impl;

import com.turkcell.analyticsservice.dto.ForUserDto.CreateExampleDto;
import com.turkcell.analyticsservice.dto.ForUserDto.LoginExampleDto;
import com.turkcell.analyticsservice.kafka.AnalyticsPipeline;
import com.turkcell.analyticsservice.model.EventType;
import com.turkcell.analyticsservice.model.UserCreateBehavior;
import com.turkcell.analyticsservice.model.UserLoginBehavior;
import com.turkcell.analyticsservice.repository.UserCreateBehaviorRepository;
import com.turkcell.analyticsservice.repository.UserLoginBehaviorRepository;
import com.turkcell.analyticsservice.service.UserBehaviorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserBehaviorServiceServiceImpl implements UserBehaviorService {

    private final UserMetrics userMetrics;
    private final UserCreateBehaviorRepository userBehaviorRepository;
    private final UserLoginBehaviorRepository userLoginBehaviorRepository;
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsPipeline.class);

    public void registerAnalyticsToUser(CreateExampleDto exampleDto){
        logger.info("Registering analytics to user start ");
        UserCreateBehavior userBehavior = new UserCreateBehavior();
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

    @Override
    public void loginAnalyticsToUser(LoginExampleDto exampleDto) {
        logger.info("Logging user login analytics start");
        // dikkat et denemeden önce
        //long loginStartTime = System.currentTimeMillis(); // Epoch millis cinsinden istek bu şekilde gelecek
        try {
            // 1. Entity oluştur
            UserLoginBehavior loginBehavior = new UserLoginBehavior();
            loginBehavior.setEmail(exampleDto.getEmail());
            loginBehavior.setLoginStartTime(exampleDto.getLoginStartTime());
            loginBehavior.setEventType(EventType.valueOf(exampleDto.getEventType()));


            // 2. Veritabanına kaydet
            userLoginBehaviorRepository.save(loginBehavior);
            logger.info("User login behavior saved: {}", loginBehavior);
            long durationMs = System.currentTimeMillis() - exampleDto.getLoginStartTime();

            // 3. Metrikleri güncelle
            userMetrics.getLoginTimer().record(durationMs, TimeUnit.MILLISECONDS);

            logger.info("Login analytics processed successfully: {}", loginBehavior);

        } catch (Exception e) {
            logger.error("Error processing login analytics for: {}", exampleDto, e);
            throw new RuntimeException("Login analytics failed", e);
        }
    }


}
