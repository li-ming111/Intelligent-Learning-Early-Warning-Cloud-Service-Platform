package com.academic.feign;

import com.academic.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Warning Service Feign Client
 * 调用 warning-service 的接口
 */
@FeignClient(name = "warning-service", fallback = WarningServiceFallback.class)
public interface WarningServiceClient {

    /**
     * 获取所有警告列表
     */
    @GetMapping("/warnings/list")
    ApiResponse<Object> getWarnings();

    /**
     * 根据学生ID获取警告列表
     */
    @GetMapping("/warnings/student")
    ApiResponse<Object> getWarningsByStudentId(@RequestParam("studentId") Long studentId);

    /**
     * 根据警告等级获取警告列表
     */
    @GetMapping("/warnings/level")
    ApiResponse<Object> getWarningsByLevel(@RequestParam("level") Integer level);
}
