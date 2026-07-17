package com.academic.feign;

import com.academic.common.dto.ApiResponse;
import com.academic.common.entity.Course;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CourseServiceFallback implements CourseServiceClient {

    @Override
    public ApiResponse<List<Course>> getAllCourses() {
        log.warn("Course service fallback triggered");
        return ApiResponse.success(List.of());
    }
}
