package com.turkcell.analyticsservice.service.impl;

import com.turkcell.analyticsservice.dto.dto.TicketAnalyticsDto;
import com.turkcell.analyticsservice.model.CustomerSupportBehavior;
import com.turkcell.analyticsservice.model.EventType;
import com.turkcell.analyticsservice.repository.CustomerSupportBehaviorRepository;
import com.turkcell.analyticsservice.service.CustomerSupportBehaviorService;
import io.github.bothuany.enums.TicketStatus;
import io.github.bothuany.event.analytics.TicketAnalyticsEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerSupportBehaviorServiceImpl implements CustomerSupportBehaviorService {

    private final CustomerSupportBehaviorRepository repository;
    private final TicketMetrics metrics;

    @Override
    public void processTicketCreation(TicketAnalyticsEvent ticketAnalyticsEvent) {
        saveAnalytics(ticketAnalyticsEvent);
        metrics.incrementTicketsCreated();
    }

    @Override
    public void processTicketUpdate(TicketAnalyticsEvent ticketAnalyticsEvent) {
        saveAnalytics(ticketAnalyticsEvent);
        metrics.incrementTicketsUpdated();
    }

    @Override
    public void processTicketClosure(TicketAnalyticsEvent ticketAnalyticsEvent) {
        saveAnalytics(ticketAnalyticsEvent);
        metrics.incrementTicketsClosed();
    }

    @Override
    public List<TicketAnalyticsDto> getAllTicketAnalytics() {
        List<CustomerSupportBehavior> customerSupportBehaviors = repository.findAll();
        return customerSupportBehaviors.stream().map(customerSupportBehavior -> new TicketAnalyticsDto(
                customerSupportBehavior.getId(),
                customerSupportBehavior.getTicketId(),
                customerSupportBehavior.getCustomerId(),
                customerSupportBehavior.getEventType().name(),
                customerSupportBehavior.getTicketStatus().name(),
                customerSupportBehavior.getEventTime()
        )).collect(Collectors.toList());
    }


    private void saveAnalytics(TicketAnalyticsEvent ticketAnalyticsEvent) {
        CustomerSupportBehavior analytics = CustomerSupportBehavior.builder()
                .ticketId(ticketAnalyticsEvent.getTicketId())
                .customerId(ticketAnalyticsEvent.getCustomerId())
                .eventType(EventType.valueOf(ticketAnalyticsEvent.getEventType()))
                .ticketStatus(TicketStatus.valueOf(ticketAnalyticsEvent.getStatus()))
                .eventTime(LocalDateTime.now())
                .build();
        repository.save(analytics);
    }
}
