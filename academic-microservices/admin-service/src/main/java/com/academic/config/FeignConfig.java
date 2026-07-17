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
 * Feign 客户端配置 - Token 透传 + 重试
 * admin-service 通过 Feign 调用 student/teacher/counselor/course/warning 服务时，
 * 自动携带原始请求的 JWT Token。
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

    @Bean
    RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Requested-By", "admin-service");
            requestTemplate.header("Content-Type", "application/json");

            // Token 透传：从当前 HTTP 请求获取 JWT 并传给下游
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
