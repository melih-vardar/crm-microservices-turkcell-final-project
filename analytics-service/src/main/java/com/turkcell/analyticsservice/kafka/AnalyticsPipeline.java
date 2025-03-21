package com.turkcell.analyticsservice.kafka;

import com.turkcell.analyticsservice.service.SubscriptionAnalyticsService;
import com.turkcell.analyticsservice.service.UserBehaviorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AnalyticsPipeline {

    private final UserBehaviorService userBehaviorService;
    private final SubscriptionAnalyticsService subscriptionAnalyticsService;
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsPipeline.class);





}
