package com.rishabh.RateLimiter.core;

import com.rishabh.RateLimiter.model.RateLimiterResponse;

public interface RateLimiter {
    RateLimiterResponse tryAcquire();
}
