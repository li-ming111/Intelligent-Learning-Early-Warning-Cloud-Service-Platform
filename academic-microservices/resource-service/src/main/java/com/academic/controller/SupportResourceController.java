package com.academic.controller;

import com.academic.common.dto.ApiResponse;
import com.academic.service.SupportResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Support Resource Controller
 */
@Slf4j
@RestController
@RequestMapping("/resources")
public class SupportResourceController {

    private final SupportResourceService supportResourceService;

    public SupportResourceController(SupportResourceService supportResourceService) {
        this.supportResourceService = supportResourceService;
    }

    /**
     * 获取支持资源列表
     */
    @GetMapping("/list")
    public ApiResponse<Object> getResourceList(@RequestParam(required = false) Map<String, Object> params) {
        try {
            log.info("Get resource list request: {}", params);
            Object result = supportResourceService.getResourceList(params);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("Get resource list failed: {}", e.getMessage());
            return ApiResponse.error(500, e.getMessage());
        }
    }

    /**
     * 获取资源详情
     */
    @GetMapping("/detail/{resourceId}")
    public ApiResponse<Object> getResourceDetail(@PathVariable String resourceId) {
        try {
            log.info("Get resource detail request: {}", resourceId);
            Object result = supportResourceService.getResourceDetail(resourceId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("Get resource detail failed: {}", e.getMessage());
            return ApiResponse.error(500, e.getMessage());
        }
    }

    /**
     * 新增支持资源
     */
    @PostMapping("/add")
    public ApiResponse<Object> addResource(@RequestBody Map<String, Object> request) {
        try {
            log.info("Add resource request: {}", request);
            Object result = supportResourceService.addResource(request);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("Add resource failed: {}", e.getMessage());
            return ApiResponse.error(500, e.getMessage());
        }
    }

    /**
     * 更新支持资源
     */
    @PutMapping("/update/{resourceId}")
    public ApiResponse<Object> updateResource(@PathVariable String resourceId, @RequestBody Map<String, Object> request) {
        try {
            log.info("Update resource request: {} {}", resourceId, request);
            Object result = supportResourceService.updateResource(resourceId, request);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("Update resource failed: {}", e.getMessage());
            return ApiResponse.error(500, e.getMessage());
        }
    }

    /**
     * 删除支持资源
     */
    @DeleteMapping("/delete/{resourceId}")
    public ApiResponse<Object> deleteResource(@PathVariable String resourceId) {
        try {
            log.info("Delete resource request: {}", resourceId);
            Object result = supportResourceService.deleteResource(resourceId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("Delete resource failed: {}", e.getMessage());
            return ApiResponse.error(500, e.getMessage());
        }
    }

    /**
     * 搜索支持资源
     */
    @PostMapping("/search")
    public ApiResponse<Object> searchResources(@RequestBody Map<String, Object> request) {
        try {
            log.info("Search resources request: {}", request);
            Object result = supportResourceService.searchResources(request);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("Search resources failed: {}", e.getMessage());
            return ApiResponse.error(500, e.getMessage());
        }
    }
}
