package com.academic.service;

import com.academic.common.entity.Course;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService {
    /**
     * 根据ID获取课程
     */
    Course getById(Long id);

    /**
     * 根据代码获取课程
     */
    Course getByCode(String code);

    /**
     * 保存课程
     */
    boolean save(Course course);

    /**
     * 更新课程
     */
    boolean updateById(Course course);

    /**
     * 删除课程
     */
    boolean removeById(Long id);

    /**
     * 分页查询课程
     */
    Page<Course> page(Page<Course> page, QueryWrapper<Course> queryWrapper);

    /**
     * 查询课程列表
     */
    List<Course> list(QueryWrapper<Course> queryWrapper);

    /**
     * 统计课程数量
     */
    long count(QueryWrapper<Course> queryWrapper);

    /**
     * 统计课程总数
     */
    long count();
}
