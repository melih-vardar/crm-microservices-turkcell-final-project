package com.turkcell.analyticsservice.service;

import com.turkcell.analyticsservice.dto.ForUserDto.ExampleBillDto;

public interface BillBehaviorService {
    void BillAnalyticsToCustomer(ExampleBillDto exampleBillDto);
}
