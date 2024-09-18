package com.rishabh.RateLimiter.core;

import com.rishabh.RateLimiter.model.RateLimiterResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Data
public class TokenBucketRateLimiter implements RateLimiter {

    @Autowired
    private final DistributedTokenBucket tokenBucket;

    @Override
    public RateLimiterResponse tryAcquire() {
        boolean result = tokenBucket.allowRequest();
        RateLimiterResponse rateLimiterResponse = new RateLimiterResponse();
        rateLimiterResponse.setMessage(result ? "Request allowed" : "Request denied due to rate limiting");
        rateLimiterResponse.setStatusCode(result ? 200 : 429);
        return rateLimiterResponse;
    }
}
