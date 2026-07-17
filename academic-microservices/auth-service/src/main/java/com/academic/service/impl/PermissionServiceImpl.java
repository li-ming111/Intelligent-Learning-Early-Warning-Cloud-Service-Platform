package com.academic.service.impl;

import com.academic.entity.Permission;
import com.academic.mapper.PermissionMapper;
import com.academic.service.PermissionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限服务实现类
 */
@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    public PermissionServiceImpl(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public Permission getById(Long id) {
        try {
            log.info("Get permission by id: {}", id);
            return permissionMapper.selectById(id);
        } catch (Exception e) {
            log.error("Get permission by id failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Permission getByCode(String code) {
        try {
            log.info("Get permission by code: {}", code);
            QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("code", code);
            return permissionMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("Get permission by code failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean save(Permission permission) {
        try {
            log.info("Save permission: {}", permission.getName());
            return permissionMapper.insert(permission) > 0;
        } catch (Exception e) {
            log.error("Save permission failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean updateById(Permission permission) {
        try {
            log.info("Update permission: {}", permission.getId());
            return permissionMapper.updateById(permission) > 0;
        } catch (Exception e) {
            log.error("Update permission failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean removeById(Long id) {
        try {
            log.info("Remove permission: {}", id);
            return permissionMapper.deleteById(id) > 0;
        } catch (Exception e) {
            log.error("Remove permission failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<Permission> page(Page<Permission> page, QueryWrapper<Permission> queryWrapper) {
        try {
            log.info("Page query permissions");
            return permissionMapper.selectPage(page, queryWrapper);
        } catch (Exception e) {
            log.error("Page query permissions failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Permission> list(QueryWrapper<Permission> queryWrapper) {
        try {
            log.info("List permissions");
            return permissionMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("List permissions failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public long count(QueryWrapper<Permission> queryWrapper) {
        try {
            log.info("Count permissions");
            return permissionMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            log.error("Count permissions failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public long count() {
        try {
            log.info("Count all permissions");
            return permissionMapper.selectCount(null);
        } catch (Exception e) {
            log.error("Count all permissions failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Permission> getByStatus(Integer status) {
        try {
            log.info("Get permissions by status: {}", status);
            QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", status);
            return permissionMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("Get permissions by status failed: {}", e.getMessage());
            throw e;
        }
    }
}
