package com.turkcell.analyticsservice.service;


import io.github.bothuany.event.analytics.BillAnalyticsEvent;

public interface BillBehaviorService {
    void BillAnalyticsToCustomer(BillAnalyticsEvent billAnalyticsEvent);
    
}
