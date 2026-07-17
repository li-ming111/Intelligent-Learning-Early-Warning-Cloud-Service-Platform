package com.academic.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign 客户端配置
 * 包括 Token 透传、日志级别、重试策略
 */
@Configuration
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    Retryer feignRetryer() {
        return new Retryer.Default(1000, 2000, 3);
    }

    /**
     * Token 透传拦截器
     * 将当前请求的 Authorization 头传递给下游微服务
     */
    @Bean
    RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Requested-By", "ai-service");
            requestTemplate.header("Content-Type", "application/json");

            // 传递 JWT Token 到下游服务
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null) {
                    requestTemplate.header("Authorization", authHeader);
                }
            }
        };
    }
}
