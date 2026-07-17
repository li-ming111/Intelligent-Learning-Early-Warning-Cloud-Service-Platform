package com.academic.service.impl;

import com.academic.common.entity.Course;
import com.academic.common.entity.TeacherCourse;
import com.academic.mapper.CourseMapper;
import com.academic.service.CourseService;
import com.academic.service.TeacherCourseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程服务实现类
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    private TeacherCourseService teacherCourseService;

    @Override
    public Course getByCourseCode(String courseCode) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCode, courseCode);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public boolean createCourse(Course course) {
        // 检查课程代码是否已存在
        Course existingCourse = getByCourseCode(course.getCode());
        if (existingCourse != null) {
            return false;
        }

        // 设置创建时间和更新时间
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());

        return save(course);
    }

    @Override
    public boolean updateCourse(Course course) {
        // 设置更新时间
        course.setUpdatedAt(LocalDateTime.now());
        return updateById(course);
    }

    @Override
    public boolean deleteCourse(Long id) {
        return removeById(id);
    }

    @Override
    public List<Course> getCoursesByTeacherId(Long teacherId) {
        // 直接通过 courses 表的 teacher_id 字段查询（该字段存储 users.id）
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getTeacherId, teacherId);
        return baseMapper.selectList(wrapper);
    }
}
