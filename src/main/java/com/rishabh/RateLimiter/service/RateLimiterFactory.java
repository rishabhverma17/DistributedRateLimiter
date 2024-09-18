package com.rishabh.RateLimiter.service;

import com.rishabh.RateLimiter.core.RateLimiter;
import com.rishabh.RateLimiter.core.TokenBucketRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterFactory {

    @Autowired
    private TokenBucketRateLimiter tokenBucketRateLimiter;

    public RateLimiter createRateLimiter(String type) {
        switch (type) {
            case "tokenBucket":
                return tokenBucketRateLimiter;
            default:
                throw new IllegalArgumentException("Invalid rate limiter type");
        }
    }
}
