package com.academic.service;

import com.academic.common.entity.TeacherCourse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 教师课程关联服务接口
 */
public interface TeacherCourseService {
    /**
     * 根据ID获取教师课程关联
     */
    TeacherCourse getById(Long id);

    /**
     * 保存教师课程关联
     */
    boolean save(TeacherCourse teacherCourse);

    /**
     * 更新教师课程关联
     */
    boolean updateById(TeacherCourse teacherCourse);

    /**
     * 删除教师课程关联
     */
    boolean removeById(Long id);

    /**
     * 分页查询教师课程关联
     */
    Page<TeacherCourse> page(Page<TeacherCourse> page, QueryWrapper<TeacherCourse> queryWrapper);

    /**
     * 查询教师课程关联列表
     */
    List<TeacherCourse> list(QueryWrapper<TeacherCourse> queryWrapper);

    /**
     * 根据教师ID查询课程列表
     */
    List<TeacherCourse> getByTeacherId(Long teacherId);

    /**
     * 根据课程ID查询教师列表
     */
    List<TeacherCourse> getByCourseId(Long courseId);

    /**
     * 统计教师课程关联数量
     */
    long count(QueryWrapper<TeacherCourse> queryWrapper);

    /**
     * 统计教师课程关联总数
     */
    long count();

    /**
     * 根据教师ID和课程ID获取关联
     */
    TeacherCourse getByTeacherIdAndCourseId(Long teacherId, Long courseId);
}
