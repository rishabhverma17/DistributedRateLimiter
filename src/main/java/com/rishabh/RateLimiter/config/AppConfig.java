package com.rishabh.RateLimiter.config;

import com.rishabh.RateLimiter.model.TokenBucketConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = { "com"})
public class AppConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    /** Comment while using Local Redis */
    //@Value("${spring.redis.password}")
    //private String redisPassword;

    @Value("${token.bucket.capacity}")
    private int tokenBucketCapacity;

    @Value("${token.bucket.tokensPerPeriod}")
    private int tokensPerPeriod;

    @Value("${token.bucket.periodSeconds}")
    private int periodSeconds;

    @Value("${token.bucket.key}")
    private String tokenKey;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        //** Uncomment while using Local Redis */
        return new LettuceConnectionFactory(redisHost, redisPort);

        //** Uncomment while using Azure Redis */
        /*RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        redisConfig.setPassword(RedisPassword.of(redisPassword));
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .useSsl()
                .build();
        return new LettuceConnectionFactory(redisConfig, clientConfig); */
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    public TokenBucketConfig tokenBucketConfig() {
        return new TokenBucketConfig(tokenBucketCapacity, tokensPerPeriod, periodSeconds, tokenKey);
    }
}


