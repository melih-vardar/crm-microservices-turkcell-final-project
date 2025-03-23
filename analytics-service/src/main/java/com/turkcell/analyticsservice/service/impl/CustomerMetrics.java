package com.turkcell.analyticsservice.service.impl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CustomerMetrics {

    private final Counter CustomerRegistrationsCounter;

    public CustomerMetrics(MeterRegistry meterRegistry) {
        this.CustomerRegistrationsCounter = Counter.builder("user.registrations.total")
                .description("total user registered count")
                .tag("application","analytics-service").register(meterRegistry);
    }

    public void incrementCustomerRegistrations() {
        this.CustomerRegistrationsCounter.increment();
    }


}
