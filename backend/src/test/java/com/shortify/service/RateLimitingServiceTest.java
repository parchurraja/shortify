package com.shortify.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitingServiceTest {

    private RateLimitingService rateLimitingService;

    @BeforeEach
    void setUp() {
        rateLimitingService = new RateLimitingService();
    }

    @Test
    @DisplayName("Should allow requests within limit and block when exceeded")
    void testRateLimitingLimits() {
        String key = "test-key";
        int limit = 3;

        // First 3 requests should be allowed
        assertTrue(rateLimitingService.tryConsume(key, limit, Duration.ofMinutes(1)));
        assertTrue(rateLimitingService.tryConsume(key, limit, Duration.ofMinutes(1)));
        assertTrue(rateLimitingService.tryConsume(key, limit, Duration.ofMinutes(1)));

        // 4th request should exceed the limit
        assertFalse(rateLimitingService.tryConsume(key, limit, Duration.ofMinutes(1)));
    }

    @Test
    @DisplayName("Should use separate buckets for different keys")
    void testDifferentKeysIndependent() {
        String keyA = "key-a";
        String keyB = "key-b";
        int limit = 1;

        assertTrue(rateLimitingService.tryConsume(keyA, limit, Duration.ofMinutes(1)));
        assertFalse(rateLimitingService.tryConsume(keyA, limit, Duration.ofMinutes(1)));

        // keyB should still be allowed since it's in a separate bucket
        assertTrue(rateLimitingService.tryConsume(keyB, limit, Duration.ofMinutes(1)));
        assertFalse(rateLimitingService.tryConsume(keyB, limit, Duration.ofMinutes(1)));
    }
}
