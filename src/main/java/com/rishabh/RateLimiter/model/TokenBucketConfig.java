package com.rishabh.RateLimiter.model;

import lombok.Getter;

@Getter
public class TokenBucketConfig {
    private final int capacity;
    private final int tokensPerPeriod;
    private final int periodSeconds;
    private final String tokenKey;

    public TokenBucketConfig(int capacity, int tokensPerPeriod, int periodSeconds, String tokenKey) {
        this.capacity = capacity;
        this.tokensPerPeriod = tokensPerPeriod;
        this.periodSeconds = periodSeconds;
        this.tokenKey = tokenKey;
    }

}
