package com.turkcell.analyticsservice.service.impl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TicketMetrics {
    private final Counter ticketsCreated;
    private final Counter ticketsUpdated;
    private final Counter ticketsClosed;

    public TicketMetrics(MeterRegistry registry) {
        ticketsCreated = Counter.builder("tickets_created_total")
                .description("Total tickets created").register(registry);

        ticketsUpdated = Counter.builder("tickets_updated_total")
                .description("Total tickets updated").register(registry);

        ticketsClosed = Counter.builder("tickets_closed_total")
                .description("Total tickets closed").register(registry);
    }

    public void incrementTicketsCreated() {
        ticketsCreated.increment();
    }

    public void incrementTicketsUpdated() {
        ticketsUpdated.increment();
    }

    public void incrementTicketsClosed() {
        ticketsClosed.increment();
    }
}
