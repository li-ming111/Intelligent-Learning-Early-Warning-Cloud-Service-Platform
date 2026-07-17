package com.academic.domain.service;

import com.academic.domain.model.User;

/**
 * 用户领域服务
 */
public interface UserDomainService {
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    User findById(Long id);
    
    /**
     * 创建用户
     * @param user 用户信息
     * @return 创建后的用户信息
     */
    User create(User user);
    
    /**
     * 更新用户
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    User update(User user);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean delete(Long id);
}