package com.turkcell.analyticsservice.service.impl;

import com.turkcell.analyticsservice.dto.ForUserDto.TicketExampleDto;
import com.turkcell.analyticsservice.model.CustomerSupportBehavior;
import com.turkcell.analyticsservice.model.EventType;
import com.turkcell.analyticsservice.repository.CustomerSupportBehaviorRepository;
import com.turkcell.analyticsservice.service.CustomerSupportBehaviorService;
import io.github.bothuany.enums.TicketStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomerSupportBehaviorServiceImpl implements CustomerSupportBehaviorService {

    private final CustomerSupportBehaviorRepository repository;
    private final TicketMetrics metrics;

    @Override
    public void processTicketCreation(TicketExampleDto event) {
        saveAnalytics(event);
        metrics.incrementTicketsCreated();
    }

    @Override
    public void processTicketUpdate(TicketExampleDto event) {

    }

    @Override
    public void processTicketClosure(TicketExampleDto event) {

    }

    private void saveAnalytics(TicketExampleDto event) {
        CustomerSupportBehavior analytics = CustomerSupportBehavior.builder()
                .ticketId(event.getTicketId())
                .customerId(event.getCustomerId())
                .eventType(EventType.valueOf(event.getEventType()))
                .ticketStatus(TicketStatus.valueOf(event.getStatus()))
                .eventTime(LocalDateTime.now())
                .build();
        repository.save(analytics);
    }
}
