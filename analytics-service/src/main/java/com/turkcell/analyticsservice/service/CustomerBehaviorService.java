package com.turkcell.analyticsservice.service;

import com.turkcell.analyticsservice.dto.ForUserDto.CreateExampleCustomerDto;
import com.turkcell.analyticsservice.dto.ForUserDto.CreateExampleDto;

public interface CustomerBehaviorService {

    void registerAnalyticsToCustomer(CreateExampleCustomerDto createExampleCustomerDto);
}
