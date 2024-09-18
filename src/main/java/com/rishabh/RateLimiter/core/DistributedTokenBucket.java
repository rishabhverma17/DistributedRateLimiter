package com.rishabh.RateLimiter.core;

import com.rishabh.RateLimiter.model.TokenBucketConfig;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class DistributedTokenBucket {

    private static final Logger logger = LoggerFactory.getLogger(DistributedTokenBucket.class);

    private final long capacity;
    private final long tokensPerPeriod;
    private final Duration period;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String tokenKey;

    @Autowired
    public DistributedTokenBucket(TokenBucketConfig config, RedisTemplate<String, Object> redisTemplate) {
        this.capacity = config.getCapacity();
        this.tokensPerPeriod = config.getTokensPerPeriod();
        this.period = Duration.ofSeconds(config.getPeriodSeconds());
        this.redisTemplate = redisTemplate;
        this.tokenKey = config.getTokenKey();
    }

    @PostConstruct
    public void initialize() {
        if (redisTemplate.opsForValue().get(tokenKey) == null) {
            redisTemplate.opsForValue().set(tokenKey, String.valueOf(capacity));
            redisTemplate.opsForValue().set(tokenKey + ":lastRefill", Instant.now().toString());
            logger.info("Initialized token bucket with {} tokens", capacity);
        } else {
            logger.info("Token bucket already initialized with {} tokens", redisTemplate.opsForValue().get(tokenKey));
        }
    }

    public boolean allowRequest() {
        refill();

        try {
            // Check if key exists and is a number
            if (redisTemplate.hasKey(tokenKey) && redisTemplate.opsForValue().get(tokenKey) instanceof String) {
                String value = (String) redisTemplate.opsForValue().get(tokenKey);
                try {
                    Long currentTokens = Long.parseLong(value);
                    if (currentTokens > 0) {
                        redisTemplate.opsForValue().set(tokenKey, String.valueOf(currentTokens - 1));
                        logger.info("Request allowed, remaining tokens: {}", currentTokens - 1);
                        return true;
                    }
                } catch (NumberFormatException e) {
                    logger.error("Error parsing token value: {}", e.getMessage());
                }
            }
            logger.info("Request denied, no tokens available");
            return false;
        } catch (Exception e) {
            logger.error("Error decrementing tokens: {}", e.getMessage());
            return false;
        }
    }

    private void refill() {
        Instant now = Instant.now();
        String lastRefillStr = (String) redisTemplate.opsForValue().get(tokenKey + ":lastRefill");
        Instant lastRefill = lastRefillStr != null ? Instant.parse(lastRefillStr) : now;

        long elapsedTime = Duration.between(lastRefill, now).toMillis();
        long periods = elapsedTime / period.toMillis();
        if (periods > 0) {
            long newTokens = periods * tokensPerPeriod;
            try {
                Long currentTokens = Long.valueOf(redisTemplate.opsForValue().get(tokenKey) != null ?
                        redisTemplate.opsForValue().get(tokenKey).toString() : "0");
                long updatedTokens = Math.min(capacity, currentTokens + newTokens);
                redisTemplate.opsForValue().set(tokenKey, String.valueOf(updatedTokens));
                redisTemplate.opsForValue().set(tokenKey + ":lastRefill", now.toString());
                logger.info("Refilled token bucket with {} tokens, total tokens: {}", newTokens, updatedTokens);
            } catch (Exception e) {
                logger.error("Error refilling tokens: {}", e.getMessage());
            }
        }
    }
}