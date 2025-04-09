package com.turkcell.analyticsservice.service;


import com.turkcell.analyticsservice.dto.dto.TicketAnalyticsDto;
import io.github.bothuany.event.analytics.TicketAnalyticsEvent;

import java.util.List;
import java.util.UUID;

public interface CustomerSupportBehaviorService {
    void processTicketCreation(TicketAnalyticsEvent ticketAnalyticsEvent);
    void processTicketUpdate(TicketAnalyticsEvent ticketAnalyticsEvent);
    void processTicketClosure(TicketAnalyticsEvent ticketAnalyticsEvent);

    List<TicketAnalyticsDto> getAllTicketAnalytics();
    TicketAnalyticsDto findTicketAnalyticsById(UUID customerId);
}

