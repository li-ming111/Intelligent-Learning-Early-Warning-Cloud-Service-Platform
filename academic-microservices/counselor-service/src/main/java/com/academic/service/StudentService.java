package com.academic.service;

import com.academic.common.entity.Student;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 学生服务接口
 */
public interface StudentService extends IService<Student> {
    /**
     * 根据辅导员ID获取学生列表
     * @param counselorId 辅导员ID
     * @return 学生列表
     */
    List<Student> getByCounselorId(Long counselorId);

    /**
     * 搜索学生
     * @param keyword 关键词
     * @return 学生列表
     */
    List<Student> searchStudents(String keyword);
}
