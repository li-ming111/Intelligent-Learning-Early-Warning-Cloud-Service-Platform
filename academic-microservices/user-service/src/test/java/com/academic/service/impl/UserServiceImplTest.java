package com.academic.service.impl;

import com.academic.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testLogin_Success() {
        // 准备测试数据
        Map<String, Object> request = new HashMap<>();
        request.put("username", "2023020616");
        request.put("password", "123456");
        
        // 执行测试
        Object response = userService.login(request);
        
        // 验证结果
        assertNotNull(response);
    }
    
    @Test
    void testRegister() {
        // 执行测试
        String result = userService.register(new Object());
        
        // 验证结果
        assertEquals("注册成功", result);
    }
    
    @Test
    void testGetUserInfo() {
        // 准备测试数据
        Long userId = 1L;
        
        // 执行测试
        Object info = userService.getUserInfo(userId);
        
        // 验证结果
        assertNotNull(info);
    }
}