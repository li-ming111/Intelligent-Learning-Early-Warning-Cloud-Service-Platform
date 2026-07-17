package com.academic.feign;

import com.academic.common.dto.ApiResponse;
import org.springframework.stereotype.Component;

/**
 * 辅导员服务 Feign 客户端降级实现
 */
@Component
public class CounselorServiceFallback implements CounselorServiceClient {

    @Override
    public ApiResponse<Long> countByCollege(Long collegeId) {
        return ApiResponse.success(0L);
    }
}
