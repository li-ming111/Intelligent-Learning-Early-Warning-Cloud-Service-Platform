package com.academic.service;

import com.academic.entity.RolePermission;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 角色权限关联服务接口
 */
public interface RolePermissionService {
    /**
     * 根据ID获取角色权限关联
     */
    RolePermission getById(Long id);

    /**
     * 保存角色权限关联
     */
    boolean save(RolePermission rolePermission);

    /**
     * 更新角色权限关联
     */
    boolean updateById(RolePermission rolePermission);

    /**
     * 删除角色权限关联
     */
    boolean removeById(Long id);

    /**
     * 分页查询角色权限关联
     */
    Page<RolePermission> page(Page<RolePermission> page, QueryWrapper<RolePermission> queryWrapper);

    /**
     * 查询角色权限关联列表
     */
    List<RolePermission> list(QueryWrapper<RolePermission> queryWrapper);

    /**
     * 根据角色ID查询权限列表
     */
    List<RolePermission> getByRoleId(Long roleId);

    /**
     * 根据权限ID查询角色列表
     */
    List<RolePermission> getByPermissionId(Long permissionId);

    /**
     * 统计角色权限关联数量
     */
    long count(QueryWrapper<RolePermission> queryWrapper);

    /**
     * 统计角色权限关联总数
     */
    long count();

    /**
     * 根据角色ID和权限ID获取关联
     */
    RolePermission getByRoleIdAndPermissionId(Long roleId, Long permissionId);

    /**
     * 批量保存角色权限关联
     */
    boolean saveBatch(List<RolePermission> rolePermissions);

    /**
     * 根据角色ID删除所有关联
     */
    boolean removeByRoleId(Long roleId);
}
