package com.academic.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 全局请求过滤器
 * 用于添加请求头、Session 处理等
 */
@Component
public class GlobalRequestFilter implements GlobalFilter, Ordered {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String SESSION_ID_HEADER = "X-Session-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String traceId = request.getHeaders().getFirst(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = generateTraceId();
        }

        String sessionId = request.getHeaders().getFirst(SESSION_ID_HEADER);
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = generateSessionId();
        }

        ServerHttpRequest modifiedRequest = request.mutate()
                .header(TRACE_ID_HEADER, traceId)
                .header(SESSION_ID_HEADER, sessionId)
                .build();

        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(modifiedRequest)
                .build();

        return chain.filter(modifiedExchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String generateSessionId() {
        return "sess-" + UUID.randomUUID().toString();
    }
}
