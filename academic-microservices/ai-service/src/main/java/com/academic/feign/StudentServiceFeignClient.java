package com.academic.feign;

import com.academic.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "student-service", fallback = StudentServiceFallback.class)
public interface StudentServiceFeignClient {

    /**
     * 获取学生信息
     */
    @GetMapping("/students/info/{studentId}")
    ApiResponse<Object> getStudentInfo(@PathVariable String studentId);

    /**
     * 获取学生成绩
     */
    @GetMapping("/students/scores/{studentId}")
    ApiResponse<Object> getStudentScores(@PathVariable String studentId);
}
