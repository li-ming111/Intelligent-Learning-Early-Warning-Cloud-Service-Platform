package com.academic.service;

import com.academic.common.entity.Student;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 学生服务接口
 */
public interface StudentService extends IService<Student> {
    /**
     * 获取所有学生
     * @return 学生列表
     */
    List<Student> getAllStudents();

    /**
     * 搜索学生
     * @param keyword 关键词
     * @return 学生列表
     */
    List<Student> searchStudents(String keyword);
    
    /**
     * 根据用户ID获取学生信息
     * @param userId 用户ID
     * @return 学生信息
     */
    Student getByUserId(Long userId);
}
