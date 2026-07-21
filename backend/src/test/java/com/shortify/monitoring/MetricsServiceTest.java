package com.shortify.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MetricsServiceTest {

    private MetricsService metricsService;
    private MeterRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        metricsService = new MetricsService(registry);
    }

    @Test
    @DisplayName("Should increment urls created counter")
    void testUrlsCreatedMetric() {
        metricsService.incrementUrlsCreated();
        assertEquals(1.0, registry.counter("shortify_urls_created_total").count());
    }

    @Test
    @DisplayName("Should increment redirect success/failure counters with tags")
    void testRedirectMetrics() {
        metricsService.incrementRedirectsSuccess();
        assertEquals(1.0, registry.counter("shortify_redirects_success_total").count());

        metricsService.incrementRedirectsFailure("ResourceNotFoundException");
        double failCount = registry.counter("shortify_redirects_failure_total", "reason", "ResourceNotFoundException").count();
        assertEquals(1.0, failCount);
    }

    @Test
    @DisplayName("Should increment login success/failure counters")
    void testLoginMetrics() {
        metricsService.incrementLoginSuccess();
        assertEquals(1.0, registry.counter("shortify_auth_login_success_total").count());

        metricsService.incrementLoginFailure();
        assertEquals(1.0, registry.counter("shortify_auth_login_failure_total").count());
    }

    @Test
    @DisplayName("Should record duration timers")
    void testDurationTimers() {
        metricsService.recordRedirectDuration(120);
        assertEquals(120.0, Objects.requireNonNull(registry.find("shortify_redirect_duration_seconds").timer()).totalTime(TimeUnit.MILLISECONDS));

        metricsService.recordUrlCreateDuration(250);
        assertEquals(250.0, Objects.requireNonNull(registry.find("shortify_url_create_duration_seconds").timer()).totalTime(TimeUnit.MILLISECONDS));

        metricsService.recordLoginDuration(80);
        assertEquals(80.0, Objects.requireNonNull(registry.find("shortify_login_duration_seconds").timer()).totalTime(TimeUnit.MILLISECONDS));
    }
}
