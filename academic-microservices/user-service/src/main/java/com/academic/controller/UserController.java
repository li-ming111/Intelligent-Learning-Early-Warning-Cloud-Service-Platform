package com.academic.controller;

import com.academic.common.dto.ApiResponse;
import com.academic.common.entity.User;
import com.academic.dto.UserDTO;
import com.academic.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer role) {
        try {
            log.info("Get users request: page={}, size={}, role={}", page, size, role);
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            if (role != null) {
                queryWrapper.eq("role", role);
            }
            queryWrapper.orderByDesc("created_at");
            
            Page<User> userPage = new Page<>(page, size);
            userPage = userService.page(userPage, queryWrapper);
            
            List<UserDTO> userDTOs = userPage.getRecords().stream()
                    .map(user -> {
                        UserDTO dto = new UserDTO();
                        dto.setId(user.getId());
                        dto.setUsername(user.getUsername());
                        dto.setName(user.getName());
                        dto.setEmail(user.getEmail());
                        dto.setPhone(user.getPhone());
                        dto.setRole(user.getRole());
                        dto.setStatus(user.getStatus());
                        dto.setCreatedAt(user.getCreatedAt());
                        dto.setUpdatedAt(user.getUpdatedAt());
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            Map<String, Object> responseData = Map.of(
                    "data", userDTOs,
                    "total", userPage.getTotal(),
                    "page", page,
                    "size", size,
                    "pages", userPage.getPages()
            );
            return ApiResponse.success(responseData);
        } catch (Exception e) {
            log.error("Get users failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/info/{userId}")
    public ApiResponse<UserDTO> getUserInfo(@PathVariable Long userId) {
        try {
            log.info("Get user info request: {}", userId);
            User user = userService.getById(userId);
            if (user == null) {
                return ApiResponse.error(404, "用户不存在");
            }
            
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
            dto.setRole(user.getRole());
            dto.setStatus(user.getStatus());
            dto.setCreatedAt(user.getCreatedAt());
            dto.setUpdatedAt(user.getUpdatedAt());
            
            return ApiResponse.success(dto);
        } catch (Exception e) {
            log.error("Get user info failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 创建用户
     */
    @PostMapping("/add")
    public ApiResponse<Long> addUser(@RequestBody User user) {
        try {
            log.info("Add user request: {}", user.getUsername());
            boolean saved = userService.save(user);
            if (saved) {
                return ApiResponse.success(user.getId());
            } else {
                return ApiResponse.error("创建用户失败");
            }
        } catch (Exception e) {
            log.error("Add user failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新用户
     */
    @PutMapping("/update/{userId}")
    public ApiResponse<String> updateUser(@PathVariable Long userId, @RequestBody User user) {
        try {
            log.info("Update user request: {}", userId);
            user.setId(userId);
            boolean updated = userService.updateById(user);
            if (updated) {
                return ApiResponse.success("用户已更新");
            } else {
                return ApiResponse.error("更新用户失败");
            }
        } catch (Exception e) {
            log.error("Update user failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable Long userId) {
        try {
            log.info("Delete user request: {}", userId);
            boolean deleted = userService.removeById(userId);
            if (deleted) {
                return ApiResponse.success("用户已删除");
            } else {
                return ApiResponse.error("删除用户失败");
            }
        } catch (Exception e) {
            log.error("Delete user failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 根据用户名获取用户信息
     */
    @GetMapping("/by-username/{username}")
    public ApiResponse<User> getUserByUsername(@PathVariable String username) {
        try {
            log.info("Get user by username request: {}", username);
            User user = userService.getByUsername(username);
            if (user == null) {
                return ApiResponse.error(404, "用户不存在");
            }
            return ApiResponse.success(user);
        } catch (Exception e) {
            log.error("Get user by username failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取用户总数
     */
    @GetMapping("/count")
    public ApiResponse<Long> getUserCount(@RequestParam(required = false) Integer role) {
        try {
            log.info("Get user count request: role={}", role);
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            if (role != null) {
                queryWrapper.eq("role", role);
            }
            long count = userService.count(queryWrapper);
            return ApiResponse.success(count);
        } catch (Exception e) {
            log.error("Get user count failed: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
}
