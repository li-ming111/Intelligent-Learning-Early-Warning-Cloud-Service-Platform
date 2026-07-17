package com.academic.service.impl;

import com.academic.entity.SupportResource;
import com.academic.mapper.SupportResourceMapper;
import com.academic.service.SupportResourceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SupportResourceServiceImpl implements SupportResourceService {

    private final SupportResourceMapper supportResourceMapper;

    public SupportResourceServiceImpl(SupportResourceMapper supportResourceMapper) {
        this.supportResourceMapper = supportResourceMapper;
    }

    @Override
    public Object getResourceList(Map<String, Object> params) {
        log.info("Getting resource list: {}", params);
        
        QueryWrapper<SupportResource> queryWrapper = new QueryWrapper<>();
        
        if (params != null) {
            if (params.containsKey("type")) {
                queryWrapper.eq("type", params.get("type"));
            }
            if (params.containsKey("category")) {
                queryWrapper.eq("category", params.get("category"));
            }
            if (params.containsKey("status")) {
                queryWrapper.eq("status", params.get("status"));
            }
        }

        queryWrapper.eq("status", 1)
                   .orderByDesc("created_at");
        
        List<SupportResource> resources = supportResourceMapper.selectList(queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("resources", resources);
        result.put("total", resources.size());
        
        return result;
    }

    @Override
    public Object getResourceDetail(String resourceId) {
        log.info("Getting resource detail: {}", resourceId);
        
        Long id = Long.parseLong(resourceId);
        SupportResource resource = supportResourceMapper.selectById(id);
        
        if (resource == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "资源不存在");
            return result;
        }

        resource.setDownloads(resource.getDownloads() != null ? resource.getDownloads() + 1 : 1);
        supportResourceMapper.updateById(resource);

        Map<String, Object> result = new HashMap<>();
        result.put("id", resource.getId());
        result.put("title", resource.getTitle());
        result.put("type", resource.getType());
        result.put("category", resource.getCategory());
        result.put("description", resource.getDescription());
        result.put("url", resource.getUrl());
        result.put("filePath", resource.getFilePath());
        result.put("fileSize", resource.getFileSize());
        result.put("fileType", resource.getFileType());
        result.put("downloads", resource.getDownloads());
        result.put("createdAt", resource.getCreatedAt());
        
        return result;
    }

    @Override
    public Object addResource(Map<String, Object> request) {
        log.info("Adding resource: {}", request);
        
        String title = request.get("title") != null ? request.get("title").toString() : null;
        String type = request.get("type") != null ? request.get("type").toString() : null;
        String category = request.get("category") != null ? request.get("category").toString() : null;
        String description = request.get("description") != null ? request.get("description").toString() : null;
        String url = request.get("url") != null ? request.get("url").toString() : null;
        String filePath = request.get("filePath") != null ? request.get("filePath").toString() : null;
        String fileType = request.get("fileType") != null ? request.get("fileType").toString() : null;

        if (title == null || title.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "资源标题不能为空");
            return result;
        }

        SupportResource resource = new SupportResource()
                .setTitle(title)
                .setType(type)
                .setCategory(category)
                .setDescription(description)
                .setUrl(url)
                .setFilePath(filePath)
                .setFileType(fileType)
                .setDownloads(0)
                .setStatus(1)
                .setCreatedAt(LocalDateTime.now());

        supportResourceMapper.insert(resource);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "资源添加成功");
        result.put("resourceId", resource.getId());
        
        return result;
    }

    @Override
    public Object updateResource(String resourceId, Map<String, Object> request) {
        log.info("Updating resource: {} {}", resourceId, request);
        
        Long id = Long.parseLong(resourceId);
        SupportResource resource = supportResourceMapper.selectById(id);
        
        if (resource == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "资源不存在");
            return result;
        }

        if (request.containsKey("title")) {
            resource.setTitle(request.get("title").toString());
        }
        if (request.containsKey("type")) {
            resource.setType(request.get("type").toString());
        }
        if (request.containsKey("category")) {
            resource.setCategory(request.get("category").toString());
        }
        if (request.containsKey("description")) {
            resource.setDescription(request.get("description").toString());
        }
        if (request.containsKey("url")) {
            resource.setUrl(request.get("url").toString());
        }
        if (request.containsKey("filePath")) {
            resource.setFilePath(request.get("filePath").toString());
        }
        if (request.containsKey("fileType")) {
            resource.setFileType(request.get("fileType").toString());
        }

        supportResourceMapper.updateById(resource);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "资源更新成功");
        result.put("resourceId", resourceId);
        
        return result;
    }

    @Override
    public Object deleteResource(String resourceId) {
        log.info("Deleting resource: {}", resourceId);
        
        Long id = Long.parseLong(resourceId);
        SupportResource resource = supportResourceMapper.selectById(id);
        
        if (resource == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "资源不存在");
            return result;
        }

        resource.setStatus(0);
        supportResourceMapper.updateById(resource);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "资源删除成功");
        result.put("resourceId", resourceId);
        
        return result;
    }

    @Override
    public Object searchResources(Map<String, Object> request) {
        log.info("Searching resources: {}", request);
        
        String keyword = request.get("keyword") != null ? request.get("keyword").toString() : null;
        String type = request.get("type") != null ? request.get("type").toString() : null;

        QueryWrapper<SupportResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);

        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(qw -> qw.like("title", keyword).or().like("description", keyword));
        }
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq("type", type);
        }

        queryWrapper.orderByDesc("created_at");
        List<SupportResource> resources = supportResourceMapper.selectList(queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("resources", resources);
        result.put("total", resources.size());
        
        return result;
    }
}