package com.turkcell.analyticsservice.service.impl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.concurrent.TimeUnit;

@Component
public class UserMetrics {

    private final Counter userRegistrationsCounter;
    private final Timer loginDurationTimer;
    private final Counter loginAttemptsCounter;

    public UserMetrics(MeterRegistry registry) {
        this.userRegistrationsCounter = Counter.builder("user.registrations.total")
                .description("total user registered count")
                .tag("application","analytics-service").register(registry);

        this.loginDurationTimer = Timer.builder("user.login.duration.seconds")
                .description("User login process duration in seconds")
                .tags("application", "analytics-service", "eventType", "USER_LOGIN")
                .publishPercentiles(0.5, 0.95) // Persentiller
                .register(registry);

        this.loginAttemptsCounter = Counter.builder("user.login.attempts.total")
                .description("Total user login attempts")
                .tags("application", "analytics-service", "status", "success") // Başarılı girişler
                .register(registry);

    }
    public void incrementUserRegistrations() {
        this.userRegistrationsCounter.increment();
    }
    public void recordLoginDuration(long durationMs) {
        loginDurationTimer.record(durationMs, TimeUnit.MILLISECONDS);
    }

    public void incrementLoginAttempts() {
        loginAttemptsCounter.increment();
    }
}
