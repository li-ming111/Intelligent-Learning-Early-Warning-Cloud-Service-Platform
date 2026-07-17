package com.academic.service.impl;

import com.academic.entity.Role;
import com.academic.mapper.RoleMapper;
import com.academic.service.RoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色服务实现类
 */
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public Role getById(Long id) {
        try {
            log.info("Get role by id: {}", id);
            return roleMapper.selectById(id);
        } catch (Exception e) {
            log.error("Get role by id failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Role getByCode(String code) {
        try {
            log.info("Get role by code: {}", code);
            QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("code", code);
            return roleMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("Get role by code failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean save(Role role) {
        try {
            log.info("Save role: {}", role.getName());
            return roleMapper.insert(role) > 0;
        } catch (Exception e) {
            log.error("Save role failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean updateById(Role role) {
        try {
            log.info("Update role: {}", role.getId());
            return roleMapper.updateById(role) > 0;
        } catch (Exception e) {
            log.error("Update role failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean removeById(Long id) {
        try {
            log.info("Remove role: {}", id);
            return roleMapper.deleteById(id) > 0;
        } catch (Exception e) {
            log.error("Remove role failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<Role> page(Page<Role> page, QueryWrapper<Role> queryWrapper) {
        try {
            log.info("Page query roles");
            return roleMapper.selectPage(page, queryWrapper);
        } catch (Exception e) {
            log.error("Page query roles failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Role> list(QueryWrapper<Role> queryWrapper) {
        try {
            log.info("List roles");
            return roleMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("List roles failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public long count(QueryWrapper<Role> queryWrapper) {
        try {
            log.info("Count roles");
            return roleMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            log.error("Count roles failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public long count() {
        try {
            log.info("Count all roles");
            return roleMapper.selectCount(null);
        } catch (Exception e) {
            log.error("Count all roles failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Role> getByStatus(Integer status) {
        try {
            log.info("Get roles by status: {}", status);
            QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", status);
            return roleMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("Get roles by status failed: {}", e.getMessage());
            throw e;
        }
    }
}
