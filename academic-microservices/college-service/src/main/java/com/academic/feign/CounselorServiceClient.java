package com.academic.feign;

import com.academic.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 辅导员服务 Feign 客户端
 */
@FeignClient(name = "counselor-service", fallback = CounselorServiceFallback.class)
public interface CounselorServiceClient {

    @GetMapping("/counselors/count-by-college")
    ApiResponse<Long> countByCollege(@RequestParam Long collegeId);
}
