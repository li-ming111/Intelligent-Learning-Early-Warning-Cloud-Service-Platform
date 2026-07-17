package com.academic.feign;

import com.academic.common.dto.ApiResponse;
import com.academic.common.entity.CounselorProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Counselor Service Feign Client
 * 调用 counselor-service 的接口
 */
@FeignClient(name = "counselor-service", fallback = CounselorServiceFallback.class)
public interface CounselorServiceClient {

    @GetMapping("/counselors")
    ApiResponse<List<CounselorProfile>> getCounselorsByCollege(@RequestParam(required = false) Long collegeId);

    @GetMapping("/counselors/{counselorId}")
    ApiResponse<CounselorProfile> getCounselorById(@PathVariable("counselorId") Long counselorId);

    @GetMapping("/counselors/count-by-college")
    ApiResponse<Long> countByCollege(@RequestParam Long collegeId);
}
