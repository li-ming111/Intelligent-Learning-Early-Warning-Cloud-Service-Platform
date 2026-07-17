package com.academic.common.annotation;

import com.academic.common.constant.ApiConstants;

import java.lang.annotation.*;

/**
 * 角色权限注解
 * 用于标记方法允许访问的角色。Gateway 层已做角色-路径拦截，
 * 此注解提供方法级的二次防御。
 *
 * 使用示例：
 * {@code @RequireRole({ApiConstants.ROLE_ADMIN})}
 * {@code @RequireRole({ApiConstants.ROLE_TEACHER, ApiConstants.ROLE_COUNSELOR})}
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {

    /**
     * 允许访问的角色 ID 数组
     * 1=学生 2=教师 3=辅导员 4=管理员
     */
    int[] value() default {
            ApiConstants.ROLE_STUDENT,
            ApiConstants.ROLE_TEACHER,
            ApiConstants.ROLE_COUNSELOR,
            ApiConstants.ROLE_ADMIN
    };

    /**
     * 权限描述（用于日志）
     */
    String description() default "";
}
