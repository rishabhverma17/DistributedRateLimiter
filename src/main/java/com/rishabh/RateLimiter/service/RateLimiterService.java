package com.rishabh.RateLimiter.service;

import com.rishabh.RateLimiter.core.DistributedTokenBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    private final DistributedTokenBucket tokenBucket;

    @Autowired
    public RateLimiterService(DistributedTokenBucket tokenBucket) {
        this.tokenBucket = tokenBucket;
    }

    public boolean processRequest() {
        if (tokenBucket.allowRequest()) {
            System.out.println("Request allowed");
            // Process the request
            return true;
        } else {
            System.out.println("Request denied");
            // Handle rate limiting
            return false;
        }
    }
}
