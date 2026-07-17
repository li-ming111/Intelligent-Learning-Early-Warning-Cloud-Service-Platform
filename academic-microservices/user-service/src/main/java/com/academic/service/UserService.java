package com.academic.service;

import com.academic.common.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 根据ID获取用户
     */
    User getById(Long id);

    /**
     * 根据用户名获取用户
     */
    User getByUsername(String username);

    /**
     * 保存用户
     */
    boolean save(User user);

    /**
     * 更新用户
     */
    boolean updateById(User user);

    /**
     * 删除用户
     */
    boolean removeById(Long id);

    /**
     * 分页查询用户
     */
    Page<User> page(Page<User> page, QueryWrapper<User> queryWrapper);

    /**
     * 查询用户列表
     */
    List<User> list(QueryWrapper<User> queryWrapper);

    /**
     * 统计用户数量
     */
    long count(QueryWrapper<User> queryWrapper);

    /**
     * 统计用户总数
     */
    long count();

    /**
     * 用户登录
     */
    Object login(Map<String, Object> request);

    /**
     * 用户注册
     */
    String register(Object request);

    /**
     * 获取用户信息
     */
    Object getUserInfo(Long userId);

    /**
     * 用户登出
     */
    Object logout(Long userId, String role);

    /**
     * 刷新令牌
     */
    Object refreshToken(String refreshToken);
    
    void changePassword(Long userId, String oldPassword, String newPassword);
}
