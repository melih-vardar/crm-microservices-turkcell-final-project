package com.turkcell.analyticsservice.service.impl;

import com.turkcell.analyticsservice.dto.ForUserDto.CreateExampleCustomerDto;
import com.turkcell.analyticsservice.kafka.AnalyticsPipeline;
import com.turkcell.analyticsservice.model.CustomerCreateBehavior;
import com.turkcell.analyticsservice.model.EventType;
import com.turkcell.analyticsservice.repository.CustomerCreateBehaviorRepository;
import com.turkcell.analyticsservice.service.CustomerBehaviorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomerBehaviorServiceImp implements CustomerBehaviorService {

    private final CustomerCreateBehaviorRepository customerCreateBehaviorRepository;
    private final CustomerMetrics customerMetrics;
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsPipeline.class);

    @Override
    public void registerAnalyticsToCustomer(CreateExampleCustomerDto createExampleCustomerDto) {
        logger.info("Registering analytics to Customer start ");

        CustomerCreateBehavior customerCreateBehavior = new CustomerCreateBehavior();
        customerCreateBehavior.setCustomerId(createExampleCustomerDto.getCustomerId());
        customerCreateBehavior.setUsername(customerCreateBehavior.getUsername());
        customerCreateBehavior.setEmail(customerCreateBehavior.getEmail());
        customerCreateBehavior.setEventType(EventType.valueOf(createExampleCustomerDto.getEventType()));
        customerCreateBehavior.setDateTime(LocalDateTime.now());

        logger.info("converted userBehavior {}", customerCreateBehavior);

        customerCreateBehaviorRepository.save(customerCreateBehavior);
        customerMetrics.incrementCustomerRegistrations();
    }
}
