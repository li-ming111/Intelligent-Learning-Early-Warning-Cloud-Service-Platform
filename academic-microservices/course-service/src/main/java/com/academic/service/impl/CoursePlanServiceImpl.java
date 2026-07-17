package com.academic.service.impl;

import com.academic.entity.CoursePlan;
import com.academic.mapper.CoursePlanMapper;
import com.academic.service.CoursePlanService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程计划服务实现类
 */
@Service
public class CoursePlanServiceImpl extends ServiceImpl<CoursePlanMapper, CoursePlan> implements CoursePlanService {

    @Override
    public List<CoursePlan> getPlansByCourseId(Long courseId) {
        LambdaQueryWrapper<CoursePlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoursePlan::getCourseId, courseId);
        wrapper.eq(CoursePlan::getStatus, 1);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<CoursePlan> getPlansBySemesterAndYear(String semester, Integer year) {
        LambdaQueryWrapper<CoursePlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoursePlan::getSemester, semester);
        wrapper.eq(CoursePlan::getYear, year);
        wrapper.eq(CoursePlan::getStatus, 1);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean createCoursePlan(CoursePlan coursePlan) {
        // 设置创建时间和更新时间
        coursePlan.setCreatedAt(LocalDateTime.now());
        coursePlan.setUpdatedAt(LocalDateTime.now());
        coursePlan.setStatus(1);

        return save(coursePlan);
    }

    @Override
    public boolean updateCoursePlan(CoursePlan coursePlan) {
        // 设置更新时间
        coursePlan.setUpdatedAt(LocalDateTime.now());
        return updateById(coursePlan);
    }

    @Override
    public boolean deleteCoursePlan(Long id) {
        return removeById(id);
    }
}
