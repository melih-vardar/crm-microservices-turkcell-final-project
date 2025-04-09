package com.turkcell.analyticsservice.service;

import com.turkcell.analyticsservice.dto.dto.CustomerAnalyticDto;
import io.github.bothuany.event.analytics.CreateExampleCustomerEvent;

import java.util.List;
import java.util.UUID;

public interface CustomerBehaviorService {

    void registerAnalyticsToCustomer(CreateExampleCustomerEvent createExampleCustomerEvent);
    public List<CustomerAnalyticDto> getAllCustomerBehaviors();
    public CustomerAnalyticDto getCustomerAnalytic(UUID customerId);
}
