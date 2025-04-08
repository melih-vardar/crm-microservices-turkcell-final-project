package com.turkcell.analyticsservice.service.impl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TicketMetrics {
    private final Counter ticketsCreatedCounter;
    private final Counter ticketsUpdatedCounter;
    private final Counter ticketsClosedCounter;

    public TicketMetrics(MeterRegistry meterRegistry) {
        this.ticketsCreatedCounter = Counter.builder("tickets.created.total")
                .description("Total number of tickets created")
                .tag("application", "analytics-service")
                .register(meterRegistry);

        this.ticketsUpdatedCounter = Counter.builder("tickets.updated.total")
                .description("Total number of tickets updated")
                .tag("application", "analytics-service")
                .register(meterRegistry);

        this.ticketsClosedCounter = Counter.builder("tickets.closed.total")
                .description("Total number of tickets closed")
                .tag("application", "analytics-service")
                .register(meterRegistry);
    }

    public void incrementTicketsCreated() {
        ticketsCreatedCounter.increment();
    }

    public void incrementTicketsUpdated() {
        ticketsUpdatedCounter.increment();
    }

    public void incrementTicketsClosed() {
        ticketsClosedCounter.increment();
    }
}
