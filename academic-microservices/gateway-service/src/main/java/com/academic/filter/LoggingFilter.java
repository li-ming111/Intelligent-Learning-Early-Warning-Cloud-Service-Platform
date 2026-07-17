package com.academic.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * 全局日志过滤器
 * 记录请求和响应的日志信息
 */
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Instant startTime = Instant.now();
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();

        logger.info("请求路径: {} {}, 开始时间: {}", method, path, startTime);

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            int statusCode = exchange.getResponse().getStatusCode() != null
                    ? exchange.getResponse().getStatusCode().value()
                    : 0;

            logger.info("请求完成: {} {}, 状态码: {}, 耗时: {}ms",
                    method, path, statusCode, duration.toMillis());
        }));
    }

    @Override
    public int getOrder() {
        return -90;
    }
}
