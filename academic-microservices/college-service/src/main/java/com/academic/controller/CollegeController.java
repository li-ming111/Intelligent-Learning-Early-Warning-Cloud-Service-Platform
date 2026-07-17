package com.academic.controller;

import com.academic.common.dto.ApiResponse;
import com.academic.common.entity.College;
import com.academic.feign.CounselorServiceClient;
import com.academic.feign.StudentServiceClient;
import com.academic.feign.TeacherServiceClient;
import com.academic.service.CollegeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学院控制器
 */
@Slf4j
@RestController
@RequestMapping("/colleges")
public class CollegeController {

    private final CollegeService collegeService;
    private final StudentServiceClient studentServiceClient;
    private final TeacherServiceClient teacherServiceClient;
    private final CounselorServiceClient counselorServiceClient;

    public CollegeController(CollegeService collegeService, 
                            StudentServiceClient studentServiceClient,
                            TeacherServiceClient teacherServiceClient,
                            CounselorServiceClient counselorServiceClient) {
        this.collegeService = collegeService;
        this.studentServiceClient = studentServiceClient;
        this.teacherServiceClient = teacherServiceClient;
        this.counselorServiceClient = counselorServiceClient;
    }

    /**
     * 获取学院列表
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getColleges(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            log.info("Get colleges request: page={}, size={}", page, size);
            QueryWrapper<College> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("created_at");
            
            Page<College> collegePage = new Page<>(page, size);
            collegePage = collegeService.page(collegePage, queryWrapper);
            
            Map<String, Object> responseData = Map.of(
                    "data", collegePage.getRecords(),
                    "total", collegePage.getTotal(),
                    "page", page,
                    "size", size,
                    "pages", collegePage.getPages()
            );
            return ApiResponse.success(responseData);
        } catch (Exception e) {
            log.error("Get colleges failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学院详情
     */
    @GetMapping("/info/{collegeId}")
    public ApiResponse<Map<String, Object>> getCollegeInfo(@PathVariable Long collegeId) {
        try {
            log.info("Get college info request: {}", collegeId);
            College college = collegeService.getById(collegeId);
            if (college == null) {
                return ApiResponse.error(404, "学院不存在");
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", college.getId());
            result.put("name", college.getName());
            result.put("code", college.getCode());
            result.put("description", college.getDescription());
            result.put("studentCount", getStudentCount(collegeId));
            result.put("teacherCount", getTeacherCount(collegeId));
            result.put("counselorCount", getCounselorCount(collegeId));
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("Get college info failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 创建学院
     */
    @PostMapping("/add")
    public ApiResponse<Long> addCollege(@RequestBody College college) {
        try {
            log.info("Add college request: {}", college.getName());
            boolean saved = collegeService.save(college);
            if (saved) {
                return ApiResponse.success(college.getId());
            } else {
                return ApiResponse.error("创建学院失败");
            }
        } catch (Exception e) {
            log.error("Add college failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新学院
     */
    @PutMapping("/update/{collegeId}")
    public ApiResponse<String> updateCollege(@PathVariable Long collegeId, @RequestBody College college) {
        try {
            log.info("Update college request: {}", collegeId);
            college.setId(collegeId);
            boolean updated = collegeService.updateById(college);
            if (updated) {
                return ApiResponse.success("学院已更新");
            } else {
                return ApiResponse.error("更新学院失败");
            }
        } catch (Exception e) {
            log.error("Update college failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除学院
     */
    @DeleteMapping("/delete/{collegeId}")
    public ApiResponse<String> deleteCollege(@PathVariable Long collegeId) {
        try {
            log.info("Delete college request: {}", collegeId);
            boolean deleted = collegeService.removeById(collegeId);
            if (deleted) {
                return ApiResponse.success("学院已删除");
            } else {
                return ApiResponse.error("删除学院失败");
            }
        } catch (Exception e) {
            log.error("Delete college failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 根据代码获取学院信息
     */
    @GetMapping("/by-code/{code}")
    public ApiResponse<College> getCollegeByCode(@PathVariable String code) {
        try {
            log.info("Get college by code request: {}", code);
            College college = collegeService.getByCode(code);
            if (college == null) {
                return ApiResponse.error(404, "学院不存在");
            }
            return ApiResponse.success(college);
        } catch (Exception e) {
            log.error("Get college by code failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取所有学院列表（不分页，包含统计数据）
     */
    @GetMapping("/all")
    public ApiResponse<List<Map<String, Object>>> getAllColleges() {
        try {
            log.info("Get all colleges request");
            List<College> colleges = collegeService.list(new QueryWrapper<>());
            
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (College college : colleges) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", college.getId());
                item.put("name", college.getName());
                item.put("code", college.getCode());
                item.put("description", college.getDescription());
                item.put("studentCount", getStudentCount(college.getId()));
                item.put("teacherCount", getTeacherCount(college.getId()));
                item.put("counselorCount", getCounselorCount(college.getId()));
                resultList.add(item);
            }
            
            return ApiResponse.success(resultList);
        } catch (Exception e) {
            log.error("Get all colleges failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学院总数
     */
    @GetMapping("/count")
    public ApiResponse<Long> getCollegeCount() {
        try {
            log.info("Get college count request");
            long count = collegeService.count();
            return ApiResponse.success(count);
        } catch (Exception e) {
            log.error("Get college count failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    private Long getStudentCount(Long collegeId) {
        try {
            ApiResponse<Long> response = studentServiceClient.countByCollege(collegeId);
            if (response != null && response.getCode() == 200 && response.getData() != null) {
                return response.getData();
            }
        } catch (Exception e) {
            log.warn("Failed to get student count for college {}: {}", collegeId, e.getMessage());
        }
        return 0L;
    }

    private Long getTeacherCount(Long collegeId) {
        try {
            ApiResponse<Long> response = teacherServiceClient.countByCollege(collegeId);
            if (response != null && response.getCode() == 200 && response.getData() != null) {
                return response.getData();
            }
        } catch (Exception e) {
            log.warn("Failed to get teacher count for college {}: {}", collegeId, e.getMessage());
        }
        return 0L;
    }

    private Long getCounselorCount(Long collegeId) {
        try {
            ApiResponse<Long> response = counselorServiceClient.countByCollege(collegeId);
            if (response != null && response.getCode() == 200 && response.getData() != null) {
                return response.getData();
            }
        } catch (Exception e) {
            log.warn("Failed to get counselor count for college {}: {}", collegeId, e.getMessage());
        }
        return 0L;
    }
}
