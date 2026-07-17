package com.academic.service;

import com.academic.entity.CoursePlan;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 课程计划服务接口
 */
public interface CoursePlanService extends IService<CoursePlan> {
    /**
     * 根据课程ID获取课程计划列表
     * @param courseId 课程ID
     * @return 课程计划列表
     */
    List<CoursePlan> getPlansByCourseId(Long courseId);

    /**
     * 根据学期和年份获取课程计划列表
     * @param semester 学期
     * @param year 年份
     * @return 课程计划列表
     */
    List<CoursePlan> getPlansBySemesterAndYear(String semester, Integer year);

    /**
     * 创建课程计划
     * @param coursePlan 课程计划信息
     * @return 是否创建成功
     */
    boolean createCoursePlan(CoursePlan coursePlan);

    /**
     * 更新课程计划
     * @param coursePlan 课程计划信息
     * @return 是否更新成功
     */
    boolean updateCoursePlan(CoursePlan coursePlan);

    /**
     * 删除课程计划
     * @param id 课程计划ID
     * @return 是否删除成功
     */
    boolean deleteCoursePlan(Long id);
}
