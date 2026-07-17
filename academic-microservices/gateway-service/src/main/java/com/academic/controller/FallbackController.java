package com.academic.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 网关熔断降级控制器
 * 当后端服务不可用时，返回友好的降级响应
 */
@RestController
public class FallbackController {

    @RequestMapping("/fallback/user")
    public Map<String, Object> userFallback() {
        return buildFallback("用户服务", "user");
    }

    @RequestMapping("/fallback/college")
    public Map<String, Object> collegeFallback() {
        return buildFallback("学院服务", "college");
    }

    @RequestMapping("/fallback/course")
    public Map<String, Object> courseFallback() {
        return buildFallback("课程服务", "course");
    }

    @RequestMapping("/fallback/student")
    public Map<String, Object> studentFallback() {
        return buildFallback("学生服务", "student");
    }

    @RequestMapping("/fallback/teacher")
    public Map<String, Object> teacherFallback() {
        return buildFallback("教师服务", "teacher");
    }

    @RequestMapping("/fallback/counselor")
    public Map<String, Object> counselorFallback() {
        return buildFallback("辅导员服务", "counselor");
    }

    @RequestMapping("/fallback/admin")
    public Map<String, Object> adminFallback() {
        return buildFallback("管理服务", "admin");
    }

    @RequestMapping("/fallback/warning")
    public Map<String, Object> warningFallback() {
        return buildFallback("预警服务", "warning");
    }

    @RequestMapping("/fallback/ai")
    public Map<String, Object> aiFallback() {
        return buildFallback("AI分析服务", "ai");
    }

    @RequestMapping("/fallback/message")
    public Map<String, Object> messageFallback() {
        return buildFallback("消息服务", "message");
    }

    @RequestMapping("/fallback/resource")
    public Map<String, Object> resourceFallback() {
        return buildFallback("资源服务", "resource");
    }

    private Map<String, Object> buildFallback(String serviceName, String code) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("success", false);
        result.put("message", serviceName + "暂时不可用，请稍后重试");
        result.put("service", code);
        return result;
    }
}
