package com.turkcell.analyticsservice.service.impl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;


@Component
public class BillMetrics {

    private final Counter BillCounter;

    public BillMetrics(MeterRegistry registry) {
        this.BillCounter = Counter.builder("bill.total.count")
                .description("total bill count")
                .tag("application","analytics-service").register(registry);
    }

    public void incrementBill() {
        this.BillCounter.increment();
    }

}

