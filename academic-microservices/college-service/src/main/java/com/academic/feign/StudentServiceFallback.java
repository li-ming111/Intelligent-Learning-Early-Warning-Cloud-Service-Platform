package com.academic.feign;

import com.academic.common.dto.ApiResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 学生服务 Feign 客户端降级实现
 */
@Component
public class StudentServiceFallback implements StudentServiceClient {

    @Override
    public ApiResponse<Long> countByCollege(Long collegeId) {
        return ApiResponse.success(0L);
    }

    @Override
    public ApiResponse<List<Map<String, Object>>> getStudentsByCollege(Long collegeId) {
        return ApiResponse.success(Collections.emptyList());
    }
}
