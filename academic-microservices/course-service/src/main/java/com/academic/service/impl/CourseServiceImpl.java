package com.academic.service.impl;

import com.academic.common.entity.Course;
import com.academic.mapper.CourseMapper;
import com.academic.service.CourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程服务实现类
 */
@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;

    public CourseServiceImpl(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    @Override
    public Course getById(Long id) {
        try {
            log.info("Get course by id: {}", id);
            return courseMapper.selectById(id);
        } catch (Exception e) {
            log.error("Get course by id failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Course getByCode(String code) {
        try {
            log.info("Get course by code: {}", code);
            QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("code", code);
            return courseMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("Get course by code failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean save(Course course) {
        try {
            log.info("Save course: {}", course.getName());
            return courseMapper.insert(course) > 0;
        } catch (Exception e) {
            log.error("Save course failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean updateById(Course course) {
        try {
            log.info("Update course: {}", course.getId());
            return courseMapper.updateById(course) > 0;
        } catch (Exception e) {
            log.error("Update course failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean removeById(Long id) {
        try {
            log.info("Remove course: {}", id);
            return courseMapper.deleteById(id) > 0;
        } catch (Exception e) {
            log.error("Remove course failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<Course> page(Page<Course> page, QueryWrapper<Course> queryWrapper) {
        try {
            log.info("Page query courses");
            return courseMapper.selectPage(page, queryWrapper);
        } catch (Exception e) {
            log.error("Page query courses failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Course> list(QueryWrapper<Course> queryWrapper) {
        try {
            log.info("List courses");
            return courseMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("List courses failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public long count(QueryWrapper<Course> queryWrapper) {
        try {
            log.info("Count courses");
            return courseMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            log.error("Count courses failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public long count() {
        try {
            log.info("Count all courses");
            return courseMapper.selectCount(null);
        } catch (Exception e) {
            log.error("Count all courses failed: {}", e.getMessage());
            throw e;
        }
    }
}
