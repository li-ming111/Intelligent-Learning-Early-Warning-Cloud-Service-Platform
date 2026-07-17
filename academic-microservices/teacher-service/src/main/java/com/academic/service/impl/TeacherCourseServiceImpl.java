package com.academic.service.impl;

import com.academic.common.entity.TeacherCourse;
import com.academic.mapper.TeacherCourseMapper;
import com.academic.service.TeacherCourseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教师课程关系服务实现
 */
@Service
public class TeacherCourseServiceImpl extends ServiceImpl<TeacherCourseMapper, TeacherCourse> implements TeacherCourseService {

    @Override
    public List<TeacherCourse> getCoursesByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherCourse::getTeacherId, teacherId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<TeacherCourse> getTeachersByCourseId(Long courseId) {
        LambdaQueryWrapper<TeacherCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherCourse::getCourseId, courseId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean assignCourse(TeacherCourse teacherCourse) {
        // 设置创建时间和更新时间
        teacherCourse.setCreatedAt(LocalDateTime.now());
        teacherCourse.setUpdatedAt(LocalDateTime.now());

        return save(teacherCourse);
    }

    @Override
    public boolean updateTeacherCourse(TeacherCourse teacherCourse) {
        // 设置更新时间
        teacherCourse.setUpdatedAt(LocalDateTime.now());
        return updateById(teacherCourse);
    }

    @Override
    public boolean cancelCourseAssignment(Long id) {
        return removeById(id);
    }
}
