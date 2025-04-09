package com.turkcell.analyticsservice.service;


import com.turkcell.analyticsservice.dto.dto.BillAnalyticsDto;
import io.github.bothuany.event.analytics.BillAnalyticsEvent;

import java.util.List;

public interface BillBehaviorService {
    void BillAnalyticsToCustomer(BillAnalyticsEvent billAnalyticsEvent);
    List<BillAnalyticsDto> getAllBillAnalytics();
    BillAnalyticsDto getBillAnalyticsById(String id);
}
