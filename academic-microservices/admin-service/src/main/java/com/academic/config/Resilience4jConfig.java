package com.academic.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Resilience4j 熔断器配置
 */
@Slf4j
@Configuration
public class Resilience4jConfig {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.ofDefaults();
    }

    /**
     * 为 warning-service 配置熔断器
     */
    @Bean(name = "warningServiceCircuitBreaker")
    public CircuitBreaker warningServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                // 故障率阈值，达到该值时断路器打开
                .failureRateThreshold(50)
                // 等待时间窗口后尝试半开状态
                .waitDurationInOpenState(Duration.ofSeconds(10))
                // 滑动窗口大小
                .slidingWindowSize(100)
                // 最小调用次数
                .minimumNumberOfCalls(10)
                // 允许的半开状态下的调用次数
                .permittedNumberOfCallsInHalfOpenState(3)
                // 滑动窗口类型
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .build();

        return registry.circuitBreaker("warningService", config);
    }

    /**
     * 为 student-service 配置熔断器
     */
    @Bean(name = "studentServiceCircuitBreaker")
    public CircuitBreaker studentServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .slidingWindowSize(100)
                .minimumNumberOfCalls(10)
                .permittedNumberOfCallsInHalfOpenState(3)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .build();

        return registry.circuitBreaker("studentService", config);
    }

    /**
     * 为 counselor-service 配置熔断器
     */
    @Bean(name = "counselorServiceCircuitBreaker")
    public CircuitBreaker counselorServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .slidingWindowSize(100)
                .minimumNumberOfCalls(10)
                .permittedNumberOfCallsInHalfOpenState(3)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .build();

        return registry.circuitBreaker("counselorService", config);
    }

    /**
     * 为 message-service 配置熔断器
     */
    @Bean(name = "messageServiceCircuitBreaker")
    public CircuitBreaker messageServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .slidingWindowSize(100)
                .minimumNumberOfCalls(10)
                .permittedNumberOfCallsInHalfOpenState(3)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .build();

        return registry.circuitBreaker("messageService", config);
    }
}