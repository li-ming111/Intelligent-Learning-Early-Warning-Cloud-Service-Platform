package com.academic.service;

import com.academic.common.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService extends IService<Course> {
    /**
     * 根据类型获取课程列表
     * @param type 课程类型
     * @return 课程列表
     */
    List<Course> getByType(String type);

    /**
     * 搜索课程
     * @param keyword 关键词
     * @return 课程列表
     */
    List<Course> searchCourses(String keyword);
}
