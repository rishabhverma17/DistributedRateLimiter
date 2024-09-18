package com.rishabh.RateLimiter.annotation.aspect;

import com.rishabh.RateLimiter.annotation.RateLimit;
import com.rishabh.RateLimiter.core.RateLimiter;
import com.rishabh.RateLimiter.model.RateLimiterResponse;
import com.rishabh.RateLimiter.service.RateLimiterFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private RateLimiterFactory rateLimiterFactory;

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        RateLimiter rateLimiter = rateLimiterFactory.createRateLimiter(rateLimit.name());
        RateLimiterResponse result = rateLimiter.tryAcquire();

        if (result.getStatusCode() == 200) {
            return joinPoint.proceed();
        } else {
            return ResponseEntity.status(429).body(result);
        }
    }
}
