package com.turkcell.analyticsservice.service;

import com.turkcell.analyticsservice.dto.ForUserDto.CreateExampleCustomerDto;
import com.turkcell.analyticsservice.dto.ForUserDto.CreateExampleDto;
import io.github.bothuany.event.analytics.CreateExampleCustomerEvent;

public interface CustomerBehaviorService {

    void registerAnalyticsToCustomer(CreateExampleCustomerEvent createExampleCustomerEvent);
}
