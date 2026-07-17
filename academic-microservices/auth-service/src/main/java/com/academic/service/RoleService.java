package com.academic.service;

import com.academic.entity.Role;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {
    /**
     * 根据ID获取角色
     */
    Role getById(Long id);

    /**
     * 根据代码获取角色
     */
    Role getByCode(String code);

    /**
     * 保存角色
     */
    boolean save(Role role);

    /**
     * 更新角色
     */
    boolean updateById(Role role);

    /**
     * 删除角色
     */
    boolean removeById(Long id);

    /**
     * 分页查询角色
     */
    Page<Role> page(Page<Role> page, QueryWrapper<Role> queryWrapper);

    /**
     * 查询角色列表
     */
    List<Role> list(QueryWrapper<Role> queryWrapper);

    /**
     * 统计角色数量
     */
    long count(QueryWrapper<Role> queryWrapper);

    /**
     * 统计角色总数
     */
    long count();

    /**
     * 根据状态查询角色
     */
    List<Role> getByStatus(Integer status);
}
