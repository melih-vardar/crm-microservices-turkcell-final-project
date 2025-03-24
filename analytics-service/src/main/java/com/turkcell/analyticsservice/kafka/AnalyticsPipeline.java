package com.turkcell.analyticsservice.kafka;

import com.turkcell.analyticsservice.dto.ForUserDto.CreateExampleCustomerDto;
import com.turkcell.analyticsservice.dto.ForUserDto.CreateExampleDto;
import com.turkcell.analyticsservice.dto.ForUserDto.LoginExampleDto;
import com.turkcell.analyticsservice.dto.ForUserDto.TicketExampleDto;
import com.turkcell.analyticsservice.service.CustomerBehaviorService;
import com.turkcell.analyticsservice.service.CustomerSupportBehaviorService;
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
    private final CustomerBehaviorService customerBehaviorService;
    private final CustomerSupportBehaviorService customerSupportBehaviorService;
    @Bean
    public Consumer<CreateExampleDto> CreateAnalyticsFunction(){
        return exampleDto -> {
            try {
                logger.info("Received user analytics: {}", exampleDto);
                userBehaviorService.registerAnalyticsToUser(exampleDto);
            }catch (Exception e){
                logger.error("Failed to process user analytics function : {}",exampleDto,e);
            }
        };
    }

    @Bean
    public Consumer<LoginExampleDto> LoginAnalyticsFunction(){
        return loginExampleDto -> {
            try {
                logger.info("Received user analytics: {}", loginExampleDto);
                userBehaviorService.loginAnalyticsToUser(loginExampleDto);
            }catch (Exception e){
                logger.error("Failed to process user analytics function : {}",loginExampleDto,e);
            }
        };
    }

    @Bean
    public Consumer<CreateExampleCustomerDto> CreateAnalyticsCustomerFunction(){
        return createExampleCustomerDto -> {
          try {
              logger.info("received customer analytics: {}", createExampleCustomerDto);
              customerBehaviorService.registerAnalyticsToCustomer(createExampleCustomerDto);
          }catch (Exception e){
              logger.error("Failed to process customer analytics function : {}",createExampleCustomerDto,e);
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
