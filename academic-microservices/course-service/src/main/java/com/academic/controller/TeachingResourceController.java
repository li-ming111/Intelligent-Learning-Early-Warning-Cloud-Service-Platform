package com.academic.controller;

import com.academic.entity.TeachingResource;
import com.academic.service.TeachingResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教学资源控制器
 */
@RestController
@RequestMapping("/teaching-resources")
public class TeachingResourceController {

    @Autowired
    private TeachingResourceService teachingResourceService;

    /**
     * 获取所有教学资源
     * @return 教学资源列表
     */
    @GetMapping("/all")
    public ResponseEntity<List<TeachingResource>> getAllTeachingResources() {
        List<TeachingResource> resources = teachingResourceService.list();
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    /**
     * 根据ID获取教学资源
     * @param id 教学资源ID
     * @return 教学资源信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeachingResource> getTeachingResourceById(@PathVariable Long id) {
        TeachingResource resource = teachingResourceService.getById(id);
        if (resource != null) {
            return new ResponseEntity<>(resource, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 根据课程ID获取教学资源列表
     * @param courseId 课程ID
     * @return 教学资源列表
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<TeachingResource>> getTeachingResourcesByCourseId(@PathVariable Long courseId) {
        List<TeachingResource> resources = teachingResourceService.getResourcesByCourseId(courseId);
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    /**
     * 根据资源类型获取教学资源列表
     * @param resourceType 资源类型
     * @return 教学资源列表
     */
    @GetMapping("/type/{resourceType}")
    public ResponseEntity<List<TeachingResource>> getTeachingResourcesByType(@PathVariable String resourceType) {
        List<TeachingResource> resources = teachingResourceService.getResourcesByType(resourceType);
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    /**
     * 创建教学资源
     * @param resource 教学资源信息
     * @return 创建结果
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createTeachingResource(@RequestBody TeachingResource resource) {
        boolean success = teachingResourceService.createResource(resource);
        if (success) {
            return new ResponseEntity<>(Map.of("success", true, "message", "教学资源创建成功"), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(Map.of("success", false, "message", "教学资源创建失败"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 更新教学资源
     * @param id 教学资源ID
     * @param resource 教学资源信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTeachingResource(@PathVariable Long id, @RequestBody TeachingResource resource) {
        resource.setId(id);
        boolean success = teachingResourceService.updateResource(resource);
        if (success) {
            return new ResponseEntity<>(Map.of("success", true, "message", "教学资源更新成功"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of("success", false, "message", "教学资源更新失败"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 删除教学资源
     * @param id 教学资源ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTeachingResource(@PathVariable Long id) {
        boolean success = teachingResourceService.deleteResource(id);
        if (success) {
            return new ResponseEntity<>(Map.of("success", true, "message", "教学资源删除成功"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of("success", false, "message", "教学资源删除失败"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 搜索教学资源
     * @param keyword 关键词
     * @return 教学资源列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<TeachingResource>> searchTeachingResources(@RequestParam String keyword) {
        List<TeachingResource> resources = teachingResourceService.searchResources(keyword);
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }
}
