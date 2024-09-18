package com.rishabh.RateLimiter.controller;

import com.rishabh.RateLimiter.annotation.RateLimit;
import com.rishabh.RateLimiter.core.RateLimiter;
import com.rishabh.RateLimiter.model.RateLimiterResponse;
import com.rishabh.RateLimiter.service.RateLimiterFactory;
import com.rishabh.RateLimiter.service.RateLimiterService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
@RequestMapping("/api/ratelimit")
public class ServiceController {

    @Autowired
    private final RateLimiterService rateLimiterService;

    @Autowired
    private final RateLimiterFactory rateLimiterFactory;

    @GetMapping("/process")
    public ResponseEntity<RateLimiterResponse> processRequest() {
        boolean result = rateLimiterService.processRequest();
        RateLimiterResponse rateLimiterResponse = new RateLimiterResponse();
        rateLimiterResponse.setMessage(result ? "Request allowed" : "Request denied due to rate limiting");
        rateLimiterResponse.setStatusCode(result ? 200 : 429);
        if(result){
            return ResponseEntity.ok(rateLimiterResponse);
        }

        return ResponseEntity.status(429).body(rateLimiterResponse);
    }

    @GetMapping("/check")
    public ResponseEntity<RateLimiterResponse> canRequest() {
        RateLimiter rateLimiter = rateLimiterFactory.createRateLimiter("tokenBucket");
        RateLimiterResponse result = rateLimiter.tryAcquire();
        if(result.getStatusCode() == 200){
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.status(429).body(result);
    }

    @GetMapping("/annotationbasedratelimit")
    @RateLimit(name = "tokenBucket")
    public ResponseEntity<String> annotationBasedRateLimit() {
        return ResponseEntity.ok("Request Successful");
    }
}
