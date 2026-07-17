package com.academic.feign;

import com.academic.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 学生服务 Feign 客户端
 */
@FeignClient(name = "student-service", fallback = StudentServiceFallback.class)
public interface StudentServiceClient {

    @GetMapping("/students/count-by-college")
    ApiResponse<Long> countByCollege(@RequestParam Long collegeId);

    @GetMapping("/students/list")
    ApiResponse<List<Map<String, Object>>> getStudentsByCollege(@RequestParam(required = false) Long collegeId);
}
