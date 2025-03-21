package com.turkcell.analyticsservice.service.impl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class UserMetrics {

    private final Counter userRegistrationsCounter;
    @Getter
    private final Timer loginTimer;

    public UserMetrics(MeterRegistry registry) {
        this.userRegistrationsCounter = Counter.builder("user.registrations.total")
                .description("total user registered count")
                .tag("application","analytics-service").register(registry);

        this.loginTimer = Timer.builder("user_login_duration")
                .description("Kullanıcı giriş süreleri")
                .register(registry);
    }
    public void incrementUserRegistrations() {
        this.userRegistrationsCounter.increment();
    }

}
