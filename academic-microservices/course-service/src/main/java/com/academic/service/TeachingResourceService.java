package com.academic.service;

import com.academic.entity.TeachingResource;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 教学资源服务接口
 */
public interface TeachingResourceService extends IService<TeachingResource> {
    /**
     * 根据课程ID获取教学资源列表
     * @param courseId 课程ID
     * @return 教学资源列表
     */
    List<TeachingResource> getResourcesByCourseId(Long courseId);

    /**
     * 根据资源类型获取教学资源列表
     * @param resourceType 资源类型
     * @return 教学资源列表
     */
    List<TeachingResource> getResourcesByType(String resourceType);

    /**
     * 创建教学资源
     * @param resource 教学资源信息
     * @return 是否创建成功
     */
    boolean createResource(TeachingResource resource);

    /**
     * 更新教学资源
     * @param resource 教学资源信息
     * @return 是否更新成功
     */
    boolean updateResource(TeachingResource resource);

    /**
     * 删除教学资源
     * @param id 教学资源ID
     * @return 是否删除成功
     */
    boolean deleteResource(Long id);

    /**
     * 搜索教学资源
     * @param keyword 关键词
     * @return 教学资源列表
     */
    List<TeachingResource> searchResources(String keyword);
}
