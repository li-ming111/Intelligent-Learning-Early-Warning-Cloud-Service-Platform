package com.academic.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手拦截器
 * 用于在 WebSocket 连接建立前验证 Token
 */
@Slf4j
public class WebSocketInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("WebSocket handshake attempt from: {}", request.getRemoteAddress());
        
        // 从请求头获取 Token
        String token = request.getHeaders().getFirst("Authorization");
        
        // 如果没有 Authorization 头，尝试从查询参数获取
        if (token == null || token.isEmpty()) {
            if (request instanceof ServletServerHttpRequest) {
                ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                token = servletRequest.getServletRequest().getParameter("token");
            }
        }
        
        log.info("WebSocket Token received: {}", token != null ? "存在" : "不存在");
        
        // 将 Token 保存到 attributes 中，供后续使用
        if (token != null) {
            attributes.put("token", token);
        }
        
        // 允许连接（认证可以在后续消息处理中进行）
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        log.info("WebSocket handshake completed");
    }
}
