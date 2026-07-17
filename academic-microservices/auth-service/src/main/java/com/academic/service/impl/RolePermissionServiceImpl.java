package com.academic.service.impl;

import com.academic.entity.RolePermission;
import com.academic.mapper.RolePermissionMapper;
import com.academic.service.RolePermissionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色权限关联服务实现类
 */
@Slf4j
@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermissionMapper rolePermissionMapper;

    public RolePermissionServiceImpl(RolePermissionMapper rolePermissionMapper) {
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public RolePermission getById(Long id) {
        try {
            log.info("Get role permission by id: {}", id);
            return rolePermissionMapper.selectById(id);
        } catch (Exception e) {
            log.error("Get role permission by id failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean save(RolePermission rolePermission) {
        try {
            log.info("Save role permission: roleId={}, permissionId={}", rolePermission.getRoleId(), rolePermission.getPermissionId());
            return rolePermissionMapper.insert(rolePermission) > 0;
        } catch (Exception e) {
            log.error("Save role permission failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean updateById(RolePermission rolePermission) {
        try {
            log.info("Update role permission: {}", rolePermission.getId());
            return rolePermissionMapper.updateById(rolePermission) > 0;
        } catch (Exception e) {
            log.error("Update role permission failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean removeById(Long id) {
        try {
            log.info("Remove role permission: {}", id);
            return rolePermissionMapper.deleteById(id) > 0;
        } catch (Exception e) {
            log.error("Remove role permission failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<RolePermission> page(Page<RolePermission> page, QueryWrapper<RolePermission> queryWrapper) {
        try {
            log.info("Page query role permissions");
            return rolePermissionMapper.selectPage(page, queryWrapper);
        } catch (Exception e) {
            log.error("Page query role permissions failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<RolePermission> list(QueryWrapper<RolePermission> queryWrapper) {
        try {
            log.info("List role permissions");
            return rolePermissionMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("List role permissions failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<RolePermission> getByRoleId(Long roleId) {
        try {
            log.info("Get role permissions by role id: {}", roleId);
            QueryWrapper<RolePermission> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_id", roleId);
            return rolePermissionMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("Get role permissions by role id failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<RolePermission> getByPermissionId(Long permissionId) {
        try {
            log.info("Get role permissions by permission id: {}", permissionId);
            QueryWrapper<RolePermission> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("permission_id", permissionId);
            return rolePermissionMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("Get role permissions by permission id failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public long count(QueryWrapper<RolePermission> queryWrapper) {
        try {
            log.info("Count role permissions");
            return rolePermissionMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            log.error("Count role permissions failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public long count() {
        try {
            log.info("Count all role permissions");
            return rolePermissionMapper.selectCount(null);
        } catch (Exception e) {
            log.error("Count all role permissions failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public RolePermission getByRoleIdAndPermissionId(Long roleId, Long permissionId) {
        try {
            log.info("Get role permission by role id and permission id: roleId={}, permissionId={}", roleId, permissionId);
            QueryWrapper<RolePermission> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_id", roleId).eq("permission_id", permissionId);
            return rolePermissionMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("Get role permission by role id and permission id failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean saveBatch(List<RolePermission> rolePermissions) {
        try {
            log.info("Save batch role permissions: size={}", rolePermissions.size());
            for (RolePermission rolePermission : rolePermissions) {
                rolePermissionMapper.insert(rolePermission);
            }
            return true;
        } catch (Exception e) {
            log.error("Save batch role permissions failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean removeByRoleId(Long roleId) {
        try {
            log.info("Remove role permissions by role id: {}", roleId);
            QueryWrapper<RolePermission> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_id", roleId);
            return rolePermissionMapper.delete(queryWrapper) > 0;
        } catch (Exception e) {
            log.error("Remove role permissions by role id failed: {}", e.getMessage());
            throw e;
        }
    }
}
