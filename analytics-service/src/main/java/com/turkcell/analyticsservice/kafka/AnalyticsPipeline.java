package com.turkcell.analyticsservice.kafka;


import com.turkcell.analyticsservice.dto.ForUserDto.TicketExampleDto;
import com.turkcell.analyticsservice.service.CustomerBehaviorService;
import com.turkcell.analyticsservice.service.CustomerSupportBehaviorService;
import com.turkcell.analyticsservice.service.SubscriptionAnalyticsService;
import com.turkcell.analyticsservice.service.UserBehaviorService;
import io.github.bothuany.event.analytics.CreateExampleCustomerEvent;
import io.github.bothuany.event.analytics.CreateUserAnalyticsEvent;
import io.github.bothuany.event.analytics.LoginUserAnalyticsEvent;
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
    private final CustomerBehaviorService customerBehaviorService;
    private final CustomerSupportBehaviorService customerSupportBehaviorService;
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsPipeline.class);
    @Bean
    public Consumer<CreateUserAnalyticsEvent> CreateAnalyticsFunction(){
        return createUserAnalyticsEvent -> {
            try {
                logger.info("Received create user analytics: {}", createUserAnalyticsEvent);
                userBehaviorService.registerAnalyticsToUser(createUserAnalyticsEvent);
            }catch (Exception e){
                logger.error("Failed to process user analytics function : {}",createUserAnalyticsEvent,e);
            }
        };
    }

    @Bean
    public Consumer<LoginUserAnalyticsEvent> LoginAnalyticsFunction(){
        return loginUserAnalyticsEvent -> {
            try {
                logger.info("Received user login analytics: {}",loginUserAnalyticsEvent );
                userBehaviorService.sendUserLoginAnalytics(loginUserAnalyticsEvent);
            }catch (Exception e){
                logger.error("Failed to process user analytics function : {}",loginUserAnalyticsEvent,e);
            }
        };
    }

    @Bean
    public Consumer<CreateExampleCustomerEvent> CreateAnalyticsCustomerFunction(){
        return createExampleCustomerEvent -> {
          try {
              logger.info("received customer analytics: {}", createExampleCustomerEvent);
              customerBehaviorService.registerAnalyticsToCustomer(createExampleCustomerEvent);
          }catch (Exception e){
              logger.error("Failed to process customer analytics function : {}",createExampleCustomerEvent,e);
          }
        };
    }

    @Bean
    public Consumer<TicketExampleDto> TicketAnalyticsFunction(){
        return ticketExampleDto -> {
            switch (ticketExampleDto.getEventType()){
                case "TICKET_CREATED" -> customerSupportBehaviorService.processTicketCreation(ticketExampleDto);
                case "TICKET_UPDATED" -> customerSupportBehaviorService.processTicketUpdate(ticketExampleDto);
                case "TICKET_CLOSED" -> customerSupportBehaviorService.processTicketClosure(ticketExampleDto);
            }
        };
    }



}
