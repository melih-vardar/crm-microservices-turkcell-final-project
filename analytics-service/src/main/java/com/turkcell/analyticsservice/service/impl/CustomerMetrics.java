package com.turkcell.analyticsservice.service.impl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CustomerMetrics {

    private final Counter CustomerRegistrationsCounter;

    public CustomerMetrics(MeterRegistry meterRegistry) {
        this.CustomerRegistrationsCounter = Counter.builder("customer.registrations.total")
                .description("total customer registered count")
                .tag("application","analytics-service").register(meterRegistry);
    }

    public void incrementCustomerRegistrations() {
        this.CustomerRegistrationsCounter.increment();
    }


}
