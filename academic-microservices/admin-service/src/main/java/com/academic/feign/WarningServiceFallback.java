package com.academic.feign;

import com.academic.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Warning Service Fallback
 * 当 warning-service 不可用时的降级处理
 */
@Slf4j
@Component
public class WarningServiceFallback implements WarningServiceClient {

    @Override
    public ApiResponse<Object> getWarnings() {
        log.warn("warning-service is unavailable, returning fallback empty list");
        return ApiResponse.success(Collections.emptyList());
    }

    @Override
    public ApiResponse<Object> getWarningsByStudentId(Long studentId) {
        log.warn("warning-service is unavailable, returning fallback empty list for studentId: {}", studentId);
        return ApiResponse.success(Collections.emptyList());
    }

    @Override
    public ApiResponse<Object> getWarningsByLevel(Integer level) {
        log.warn("warning-service is unavailable, returning fallback empty list for level: {}", level);
        return ApiResponse.success(Collections.emptyList());
    }
}