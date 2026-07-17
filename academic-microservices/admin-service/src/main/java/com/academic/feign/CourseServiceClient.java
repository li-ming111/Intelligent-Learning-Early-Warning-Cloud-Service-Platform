package com.academic.feign;

import com.academic.common.dto.ApiResponse;
import com.academic.common.entity.Course;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "course-service", fallback = CourseServiceFallback.class)
public interface CourseServiceClient {

    @GetMapping("/courses/all")
    ApiResponse<List<Course>> getAllCourses();
}
