package com.academic.service.impl;

import com.academic.common.entity.User;
import com.academic.mapper.UserMapper;
import com.academic.service.UserService;
import com.academic.util.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Primary
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User getById(Long id) {
        try {
            log.info("Get user by id: {}", id);
            return userMapper.selectById(id);
        } catch (Exception e) {
            log.error("Get user by id failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public User getByUsername(String username) {
        try {
            log.info("Get user by username: {}", username);
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username);
            return userMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("Get user by username failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean save(User user) {
        try {
            log.info("Save user: {}", user.getUsername());
            user.setCreatedAt(LocalDateTime.now());
            // 自动加密密码：非BCrypt格式的明文密码统一加密
            String pwd = user.getPassword();
            if (pwd != null && !pwd.startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(pwd));
            }
            return userMapper.insert(user) > 0;
        } catch (Exception e) {
            log.error("Save user failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean updateById(User user) {
        try {
            log.info("Update user: {}", user.getId());
            // 更新时如果密码非BCrypt格式，自动加密
            String pwd = user.getPassword();
            if (pwd != null && !pwd.startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(pwd));
            }
            return userMapper.updateById(user) > 0;
        } catch (Exception e) {
            log.error("Update user failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean removeById(Long id) {
        try {
            log.info("Remove user: {}", id);
            return userMapper.deleteById(id) > 0;
        } catch (Exception e) {
            log.error("Remove user failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<User> page(Page<User> page, QueryWrapper<User> queryWrapper) {
        try {
            log.info("Page query users");
            return userMapper.selectPage(page, queryWrapper);
        } catch (Exception e) {
            log.error("Page query users failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<User> list(QueryWrapper<User> queryWrapper) {
        try {
            log.info("List users");
            return userMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("List users failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public long count(QueryWrapper<User> queryWrapper) {
        try {
            log.info("Count users");
            return userMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            log.error("Count users failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public long count() {
        try {
            log.info("Count all users");
            return userMapper.selectCount(null);
        } catch (Exception e) {
            log.error("Count all users failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Object login(Map<String, Object> request) {
        try {
            log.info("User login attempt: {}", request);
            
            String username = (String) request.get("username");
            String password = (String) request.get("password");
            
            if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "用户名和密码不能为空");
                return response;
            }
            
            Map<String, Object> response = new HashMap<>();
            
            User user = getByUsername(username);
            
            if (user != null) {
                // 检查账号状态
                if (user.getStatus() != null && user.getStatus() != 1) {
                    if (user.getStatus() == 0) {
                        response.put("message", "账号正在审批中，请等待管理员审核");
                    } else if (user.getStatus() == 2) {
                        response.put("message", "账号已被锁定");
                    } else {
                        response.put("message", "账号已被禁用");
                    }
                    response.put("status", "error");
                    return response;
                }
                // 使用BCrypt密码比对，兼容明文密码的向后兼容
                boolean passwordMatch;
                if (user.getPassword() != null && user.getPassword().startsWith("$2a$")) {
                    passwordMatch = passwordEncoder.matches(password, user.getPassword());
                } else {
                    // 向后兼容旧明文密码（自动加密）
                    passwordMatch = password.equals(user.getPassword());
                    if (passwordMatch) {
                        user.setPassword(passwordEncoder.encode(password));
                        userMapper.updateById(user);
                    }
                }
                if (passwordMatch) {
                    String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
                    String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());
                    
                    response.put("token", token);
                    response.put("refreshToken", refreshToken);
                    response.put("userId", user.getId());
                    response.put("username", user.getUsername());
                    response.put("role", user.getRole());
                    response.put("name", user.getName());
                    response.put("status", "success");
                    log.info("User login successful: {}", user.getUsername());
                } else {
                    response.put("status", "error");
                    response.put("message", "密码错误");
                    log.warn("Login failed: incorrect password for user {}", username);
                }
            } else {
                response.put("status", "error");
                response.put("message", "用户不存在");
                log.warn("Login failed: user not found {}", username);
            }
            
            return response;
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "登录失败");
            return response;
        }
    }

    @Override
    public String register(Object request) {
        try {
            log.info("User register");
            
            Map<String, Object> reqMap = (Map<String, Object>) request;
            String username = reqMap.get("username") != null ? reqMap.get("username").toString() : null;
            String password = reqMap.get("password") != null ? reqMap.get("password").toString() : null;
            String name = reqMap.get("name") != null ? reqMap.get("name").toString() : null;
            Integer role = reqMap.get("role") != null ? Integer.parseInt(reqMap.get("role").toString()) : 1;

            if (username == null || username.isEmpty()) {
                return "用户名不能为空";
            }
            if (password == null || password.length() < 6) {
                return "密码长度至少为6位";
            }

            User existingUser = getByUsername(username);
            if (existingUser != null) {
                return "用户名已存在";
            }

            User user = new User()
                    .setUsername(username)
                    .setPassword(password)
                    .setName(name != null ? name : username)
                    .setRole(role)
                    .setStatus(1);

            save(user);
            log.info("User registered successfully: {}", username);
            return "Register successful";
        } catch (Exception e) {
            log.error("Register failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Object getUserInfo(Long userId) {
        try {
            log.info("Get user info: {}", userId);
            User user = getById(userId);
            if (user != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", user.getId());
                result.put("username", user.getUsername());
                result.put("name", user.getName());
                result.put("role", user.getRole());
                result.put("status", user.getStatus());
                result.put("createdAt", user.getCreatedAt());
                return result;
            }
            return null;
        } catch (Exception e) {
            log.error("Get user info failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Object logout(Long userId, String role) {
        try {
            log.info("User logout: userId={}, role={}", userId, role);
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "Logout successful");
            return result;
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Object refreshToken(String refreshToken) {
        try {
            log.info("Refreshing token");
            
            if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
                Map<String, Object> result = new HashMap<>();
                result.put("status", "error");
                result.put("message", "无效的刷新令牌");
                return result;
            }
            
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            
            User user = getById(userId);
            Integer role = user != null ? user.getRole() : 1;
            
            String newToken = jwtUtil.generateToken(userId, username, role);
            String newRefreshToken = jwtUtil.generateRefreshToken(userId, username);
            
            Map<String, Object> result = new HashMap<>();
            result.put("token", newToken);
            result.put("refreshToken", newRefreshToken);
            result.put("status", "success");
            
            log.info("Token refreshed for user: {}", username);
            return result;
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "令牌刷新失败");
            return result;
        }
    }
    
    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) throw new RuntimeException("用户不存在");
        
        boolean match;
        if (user.getPassword().startsWith("$2a$")) {
            match = passwordEncoder.matches(oldPassword, user.getPassword());
        } else {
            match = oldPassword.equals(user.getPassword());
        }
        if (!match) throw new RuntimeException("旧密码错误");
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }
}