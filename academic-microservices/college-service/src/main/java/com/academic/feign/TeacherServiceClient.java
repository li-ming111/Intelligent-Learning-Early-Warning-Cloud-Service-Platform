package com.academic.feign;

import com.academic.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 教师服务 Feign 客户端
 */
@FeignClient(name = "teacher-service", fallback = TeacherServiceFallback.class)
public interface TeacherServiceClient {

    @GetMapping("/teachers/count-by-college")
    ApiResponse<Long> countByCollege(@RequestParam Long collegeId);
}
