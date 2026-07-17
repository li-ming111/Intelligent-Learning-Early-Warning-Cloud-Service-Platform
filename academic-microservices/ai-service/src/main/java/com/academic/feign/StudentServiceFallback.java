package com.academic.feign;

import com.academic.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * StudentService Feign 客户端降级处理
 */
@Slf4j
@Component
public class StudentServiceFallback implements StudentServiceFeignClient {

    @Override
    public ApiResponse<Object> getStudentInfo(String studentId) {
        log.warn("StudentService getStudentInfo fallback triggered for studentId: {}", studentId);
        return ApiResponse.error("学生服务暂时不可用");
    }

    @Override
    public ApiResponse<Object> getStudentScores(String studentId) {
        log.warn("StudentService getStudentScores fallback triggered for studentId: {}", studentId);
        return ApiResponse.error("学生服务暂时不可用");
    }
}