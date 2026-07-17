package com.academic.service;

import com.academic.common.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);

    /**
     * 根据ID查询用户
     */
    User findById(Long id);
}
