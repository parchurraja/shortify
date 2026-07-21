package com.shortify.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    /**
     * Resolves a bucket for a given key and consumes 1 token.
     *
     * @param key        the unique key identifying the client/user and the endpoint
     * @param capacity   the maximum capacity of the bucket
     * @param duration   the duration for replenishment
     * @return true if token was consumed successfully, false if rate limit exceeded
     */
    public boolean tryConsume(String key, long capacity, Duration duration) {
        Bucket bucket = cache.computeIfAbsent(key, k -> createNewBucket(capacity, duration));
        return bucket.tryConsume(1);
    }

    private Bucket createNewBucket(long capacity, Duration duration) {
        Bandwidth limit = Bandwidth.simple(capacity, duration);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Helper method to reset/clear cache (mostly for tests).
     */
    public void clearCache() {
        cache.clear();
    }
}
