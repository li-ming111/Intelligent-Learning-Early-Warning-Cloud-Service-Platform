package com.academic.service.impl;

import com.academic.common.entity.Course;
import com.academic.mapper.CourseMapper;
import com.academic.service.CourseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程服务实现类
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Override
    public List<Course> getByType(String type) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getType, type);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<Course> searchCourses(String keyword) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Course::getCode, keyword)
               .or()
               .like(Course::getName, keyword);
        return baseMapper.selectList(wrapper);
    }
}
