package com.academic.service.impl;

import com.academic.entity.TeachingResource;
import com.academic.mapper.TeachingResourceMapper;
import com.academic.service.TeachingResourceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教学资源服务实现类
 */
@Service
public class TeachingResourceServiceImpl extends ServiceImpl<TeachingResourceMapper, TeachingResource> implements TeachingResourceService {

    @Override
    public List<TeachingResource> getResourcesByCourseId(Long courseId) {
        LambdaQueryWrapper<TeachingResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeachingResource::getCourseId, courseId);
        wrapper.eq(TeachingResource::getStatus, 1);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<TeachingResource> getResourcesByType(String resourceType) {
        LambdaQueryWrapper<TeachingResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeachingResource::getType, resourceType);
        wrapper.eq(TeachingResource::getStatus, 1);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean createResource(TeachingResource resource) {
        // 设置创建时间和更新时间
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setStatus(1);

        return save(resource);
    }

    @Override
    public boolean updateResource(TeachingResource resource) {
        // 设置更新时间
        resource.setUpdatedAt(LocalDateTime.now());
        return updateById(resource);
    }

    @Override
    public boolean deleteResource(Long id) {
        return removeById(id);
    }

    @Override
    public List<TeachingResource> searchResources(String keyword) {
        LambdaQueryWrapper<TeachingResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(TeachingResource::getTitle, keyword)
               .or()
               .like(TeachingResource::getDescription, keyword);
        wrapper.eq(TeachingResource::getStatus, 1);
        return baseMapper.selectList(wrapper);
    }
}
