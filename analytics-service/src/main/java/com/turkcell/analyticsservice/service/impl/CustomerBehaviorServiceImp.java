package com.turkcell.analyticsservice.service.impl;

import com.turkcell.analyticsservice.dto.dto.CustomerAnalyticDto;
import com.turkcell.analyticsservice.kafka.AnalyticsPipeline;
import com.turkcell.analyticsservice.model.CustomerCreateBehavior;
import com.turkcell.analyticsservice.model.EventType;
import com.turkcell.analyticsservice.repository.CustomerCreateBehaviorRepository;
import com.turkcell.analyticsservice.service.CustomerBehaviorService;
import io.github.bothuany.event.analytics.CreateExampleCustomerEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerBehaviorServiceImp implements CustomerBehaviorService {

    private final CustomerCreateBehaviorRepository customerCreateBehaviorRepository;
    private final CustomerMetrics customerMetrics;
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsPipeline.class);

    @Override
    public void registerAnalyticsToCustomer(CreateExampleCustomerEvent createExampleCustomerEvent) {
        logger.info("Registering analytics to Customer start ");
        CustomerCreateBehavior customerCreateBehavior = new CustomerCreateBehavior();
        customerCreateBehavior.setCustomerId(createExampleCustomerEvent.getCustomerId());
        customerCreateBehavior.setFirstName(createExampleCustomerEvent.getFirstname());
        customerCreateBehavior.setLastName(createExampleCustomerEvent.getLastname());
        customerCreateBehavior.setEmail(createExampleCustomerEvent.getEmail());
        customerCreateBehavior.setEventType(EventType.valueOf(createExampleCustomerEvent.getEventType()));
        customerCreateBehavior.setDateTime(LocalDateTime.now());
        logger.info("converted userBehavior {}", customerCreateBehavior);
        customerCreateBehaviorRepository.save(customerCreateBehavior);
        customerMetrics.incrementCustomerRegistrations();
    }

    public List<CustomerAnalyticDto> getAllCustomerBehaviors() {
        List<CustomerCreateBehavior> customerBehaviors = customerCreateBehaviorRepository.findAll();
        return customerBehaviors.stream().map(customerCreateBehavior -> new CustomerAnalyticDto(
                customerCreateBehavior.getId(),
                customerCreateBehavior.getCustomerId().toString(),
                customerCreateBehavior.getFirstName(),
                customerCreateBehavior.getLastName(),
                customerCreateBehavior.getEmail(),
                customerCreateBehavior.getEventType().name(),
                customerCreateBehavior.getDateTime()
        )).collect(Collectors.toList());
    }
    @Override
    public CustomerAnalyticDto getCustomerAnalytic(UUID customerId) {
        CustomerCreateBehavior customerBehavior = customerCreateBehaviorRepository.findByCustomerId(customerId);
        CustomerAnalyticDto customerAnalyticDto = new CustomerAnalyticDto();
        customerAnalyticDto.setId(customerBehavior.getId());
        customerAnalyticDto.setCustomerId(customerBehavior.getCustomerId().toString());
        customerAnalyticDto.setFirstName(customerBehavior.getFirstName());
        customerAnalyticDto.setLastName(customerBehavior.getLastName());
        customerAnalyticDto.setEmail(customerBehavior.getEmail());
        customerAnalyticDto.setEventType(customerBehavior.getEventType().name());
        customerAnalyticDto.setDateTime(customerBehavior.getDateTime());
        return customerAnalyticDto;
    }
}
