package com.shortify.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class MetricsService {

    private final MeterRegistry registry;

    public MetricsService(MeterRegistry registry) {
        this.registry = registry;
    }

    public void incrementUrlsCreated() {
        registry.counter("shortify_urls_created_total").increment();
    }

    public void incrementRedirectsSuccess() {
        registry.counter("shortify_redirects_success_total").increment();
    }

    public void incrementRedirectsFailure(String reason) {
        registry.counter("shortify_redirects_failure_total", "reason", reason).increment();
    }

    public void incrementLoginSuccess() {
        registry.counter("shortify_auth_login_success_total").increment();
    }

    public void incrementLoginFailure() {
        registry.counter("shortify_auth_login_failure_total").increment();
    }

    public void recordRedirectDuration(long durationMs) {
        registry.timer("shortify_redirect_duration_seconds").record(Duration.ofMillis(durationMs));
    }

    public void recordUrlCreateDuration(long durationMs) {
        registry.timer("shortify_url_create_duration_seconds").record(Duration.ofMillis(durationMs));
    }

    public void recordLoginDuration(long durationMs) {
        registry.timer("shortify_login_duration_seconds").record(Duration.ofMillis(durationMs));
    }
}
