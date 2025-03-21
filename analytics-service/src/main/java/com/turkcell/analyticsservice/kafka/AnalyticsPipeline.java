package com.turkcell.analyticsservice.kafka;

import com.turkcell.analyticsservice.dto.ForUserDto.CreateExampleDto;
import com.turkcell.analyticsservice.service.SubscriptionAnalyticsService;
import com.turkcell.analyticsservice.service.UserBehaviorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class AnalyticsPipeline {

    private final UserBehaviorService userBehaviorService;
    private final SubscriptionAnalyticsService subscriptionAnalyticsService;
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsPipeline.class);

    @Bean
    public Consumer<CreateExampleDto> userAnalyticsFunction(){
        return exampleDto -> {
            try {
                logger.info("Received user analytics: {}", exampleDto);
                userBehaviorService.registerAnalyticsToUser(exampleDto);
            }catch (Exception e){
                logger.error("Failed to process user analytics function : {}",exampleDto,e);
            }
        };
    }



}
