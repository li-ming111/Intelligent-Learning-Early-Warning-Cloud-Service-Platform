package com.academic.service.impl;

import com.academic.common.entity.TeacherCourse;
import com.academic.mapper.TeacherCourseMapper;
import com.academic.service.TeacherCourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 教师课程关联服务实现类
 */
@Slf4j
@Service
public class TeacherCourseServiceImpl implements TeacherCourseService {

    private final TeacherCourseMapper teacherCourseMapper;

    public TeacherCourseServiceImpl(TeacherCourseMapper teacherCourseMapper) {
        this.teacherCourseMapper = teacherCourseMapper;
    }

    @Override
    public TeacherCourse getById(Long id) {
        try {
            log.info("Get teacher course by id: {}", id);
            return teacherCourseMapper.selectById(id);
        } catch (Exception e) {
            log.error("Get teacher course by id failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean save(TeacherCourse teacherCourse) {
        try {
            log.info("Save teacher course: teacherId={}, courseId={}", teacherCourse.getTeacherId(), teacherCourse.getCourseId());
            return teacherCourseMapper.insert(teacherCourse) > 0;
        } catch (Exception e) {
            log.error("Save teacher course failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean updateById(TeacherCourse teacherCourse) {
        try {
            log.info("Update teacher course: {}", teacherCourse.getId());
            return teacherCourseMapper.updateById(teacherCourse) > 0;
        } catch (Exception e) {
            log.error("Update teacher course failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean removeById(Long id) {
        try {
            log.info("Remove teacher course: {}", id);
            return teacherCourseMapper.deleteById(id) > 0;
        } catch (Exception e) {
            log.error("Remove teacher course failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<TeacherCourse> page(Page<TeacherCourse> page, QueryWrapper<TeacherCourse> queryWrapper) {
        try {
            log.info("Page query teacher courses");
            return teacherCourseMapper.selectPage(page, queryWrapper);
        } catch (Exception e) {
            log.error("Page query teacher courses failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<TeacherCourse> list(QueryWrapper<TeacherCourse> queryWrapper) {
        try {
            log.info("List teacher courses");
            return teacherCourseMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("List teacher courses failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<TeacherCourse> getByTeacherId(Long teacherId) {
        try {
            log.info("Get teacher courses by teacher id: {}", teacherId);
            QueryWrapper<TeacherCourse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("teacher_id", teacherId);
            return teacherCourseMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("Get teacher courses by teacher id failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<TeacherCourse> getByCourseId(Long courseId) {
        try {
            log.info("Get teacher courses by course id: {}", courseId);
            QueryWrapper<TeacherCourse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("course_id", courseId);
            return teacherCourseMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("Get teacher courses by course id failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public long count(QueryWrapper<TeacherCourse> queryWrapper) {
        try {
            log.info("Count teacher courses");
            return teacherCourseMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            log.error("Count teacher courses failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public long count() {
        try {
            log.info("Count all teacher courses");
            return teacherCourseMapper.selectCount(null);
        } catch (Exception e) {
            log.error("Count all teacher courses failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public TeacherCourse getByTeacherIdAndCourseId(Long teacherId, Long courseId) {
        try {
            log.info("Get teacher course by teacher id and course id: teacherId={}, courseId={}", teacherId, courseId);
            QueryWrapper<TeacherCourse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("teacher_id", teacherId);
            queryWrapper.eq("course_id", courseId);
            return teacherCourseMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("Get teacher course by teacher id and course id failed: {}", e.getMessage());
            throw e;
        }
    }
}
