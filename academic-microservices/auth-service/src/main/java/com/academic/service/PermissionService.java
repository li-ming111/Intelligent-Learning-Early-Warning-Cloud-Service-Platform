package com.academic.service;

import com.academic.entity.Permission;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 权限服务接口
 */
public interface PermissionService {
    /**
     * 根据ID获取权限
     */
    Permission getById(Long id);

    /**
     * 根据代码获取权限
     */
    Permission getByCode(String code);

    /**
     * 保存权限
     */
    boolean save(Permission permission);

    /**
     * 更新权限
     */
    boolean updateById(Permission permission);

    /**
     * 删除权限
     */
    boolean removeById(Long id);

    /**
     * 分页查询权限
     */
    Page<Permission> page(Page<Permission> page, QueryWrapper<Permission> queryWrapper);

    /**
     * 查询权限列表
     */
    List<Permission> list(QueryWrapper<Permission> queryWrapper);

    /**
     * 统计权限数量
     */
    long count(QueryWrapper<Permission> queryWrapper);

    /**
     * 统计权限总数
     */
    long count();

    /**
     * 根据状态查询权限
     */
    List<Permission> getByStatus(Integer status);
}
