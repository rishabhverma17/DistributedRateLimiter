package com.rishabh.RateLimiter.model;

import lombok.Data;

@Data
public class RateLimiterResponse {
    private String message;
    private Integer statusCode;
    private Boolean requestAllowed;
}
