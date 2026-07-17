package com.academic.service;

import com.academic.common.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);

    /**
     * 根据角色获取用户列表
     * @param role 角色
     * @return 用户列表
     */
    List<User> getUsersByRole(Integer role);

    /**
     * 创建用户
     * @param user 用户信息
     * @return 是否创建成功
     */
    boolean createUser(User user);

    /**
     * 更新用户
     * @param user 用户信息
     * @return 是否更新成功
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Long id);

    /**
     * 搜索用户
     * @param keyword 关键词
     * @return 用户列表
     */
    List<User> searchUsers(String keyword);

    /**
     * 验证用户登录
     * @param username 用户名
     * @param password 密码
     * @return 验证结果
     */
    boolean validateLogin(String username, String password);

    /**
     * 获取最近注册的用户
     * @param limit 返回数量
     * @return 用户列表
     */
    List<User> getRecentUsers(int limit);
}
