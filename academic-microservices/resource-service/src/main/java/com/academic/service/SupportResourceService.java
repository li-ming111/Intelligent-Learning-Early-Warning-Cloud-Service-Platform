package com.academic.service;

import java.util.Map;

public interface SupportResourceService {
    /**
     * 获取支持资源列表
     */
    Object getResourceList(Map<String, Object> params);

    /**
     * 获取资源详情
     */
    Object getResourceDetail(String resourceId);

    /**
     * 新增支持资源
     */
    Object addResource(Map<String, Object> request);

    /**
     * 更新支持资源
     */
    Object updateResource(String resourceId, Map<String, Object> request);

    /**
     * 删除支持资源
     */
    Object deleteResource(String resourceId);

    /**
     * 搜索支持资源
     */
    Object searchResources(Map<String, Object> request);
}
