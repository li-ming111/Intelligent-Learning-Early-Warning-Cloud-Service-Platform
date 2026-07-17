package com.academic.common.aspect;

import com.academic.common.annotation.RequireRole;
import com.academic.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 * @RequireRole 注解的 AOP 切面
 * 从 Gateway 注入的 X-Role 请求头读取用户角色，
 * 与注解声明的允许角色列表比对，不匹配则返回 403。
 */
@Aspect
@Component
public class RoleAspect {

    private static final Logger log = LoggerFactory.getLogger(RoleAspect.class);

    @Around("@annotation(com.academic.common.annotation.RequireRole) || " +
            "@within(com.academic.common.annotation.RequireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            // 非 Web 上下文（如定时任务、测试），直接放行
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String roleHeader = request.getHeader("X-Role");

        // 获取注解
        RequireRole requireRole = getRequireRoleAnnotation(joinPoint);
        if (requireRole == null) {
            return joinPoint.proceed();
        }

        int[] allowedRoles = requireRole.value();

        // 如果注解未指定角色（默认全部允许），直接放行
        if (allowedRoles.length == 0) {
            return joinPoint.proceed();
        }

        // 无角色信息，拒绝
        if (roleHeader == null || roleHeader.isEmpty()) {
            log.warn("缺少 X-Role 请求头，拒绝访问: {}",
                    request.getRequestURI());
            return ApiResponse.forbidden("无权访问，请先登录");
        }

        try {
            int userRole = Integer.parseInt(roleHeader);
            for (int allowed : allowedRoles) {
                if (userRole == allowed) {
                    return joinPoint.proceed();
                }
            }

            log.warn("角色不匹配: userRole={}, required={}, path={}",
                    userRole, Arrays.toString(allowedRoles), request.getRequestURI());
            return ApiResponse.forbidden("权限不足，当前角色无权访问此接口");

        } catch (NumberFormatException e) {
            log.warn("X-Role 格式错误: {}", roleHeader);
            return ApiResponse.forbidden("用户角色信息异常");
        }
    }

    private RequireRole getRequireRoleAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // 优先从方法上获取
        RequireRole methodAnnotation = signature.getMethod().getAnnotation(RequireRole.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }

        // 其次从类上获取
        return joinPoint.getTarget().getClass().getAnnotation(RequireRole.class);
    }
}
