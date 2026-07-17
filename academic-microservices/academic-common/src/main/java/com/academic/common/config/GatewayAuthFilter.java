package com.academic.common.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 从 GateWay 注入的请求头中提取用户信息，构建 Spring Security 认证上下文。
 * 由 GateWay 的 AuthenticationFilter 保证 Token 有效性，内部服务信任 Gateway 头。
 */
public class GatewayAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayAuthFilter.class);

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USERNAME = "X-Username";
    private static final String HEADER_ROLE = "X-Role";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String userId = request.getHeader(HEADER_USER_ID);
        String username = request.getHeader(HEADER_USERNAME);
        String role = request.getHeader(HEADER_ROLE);

        if (userId != null && role != null) {
            String authority = "ROLE_" + roleMapping(role);
            List<SimpleGrantedAuthority> authorities =
                    Collections.singletonList(new SimpleGrantedAuthority(authority));

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
            auth.setDetails(username);
            SecurityContextHolder.getContext().setAuthentication(auth);

            log.debug("Gateway auth established: userId={}, role={}", userId, role);
        } else {
            log.warn("缺少 Gateway 认证头: X-User-Id={}, X-Role={}, uri={}",
                    userId, role, request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }

    private String roleMapping(String role) {
        switch (role) {
            case "4": case "admin": return "ADMIN";
            case "2": case "teacher": return "TEACHER";
            case "1": case "student": return "STUDENT";
            case "3": case "counselor": return "COUNSELOR";
            case "5": case "college_admin": return "COLLEGE_ADMIN";
            default: return "USER";
        }
    }
}
