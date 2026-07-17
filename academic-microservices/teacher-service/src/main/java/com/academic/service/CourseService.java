package com.academic.service;

import com.academic.common.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService extends IService<Course> {
    /**
     * 根据课程代码获取课程信息
     * @param courseCode 课程代码
     * @return 课程信息
     */
    Course getByCourseCode(String courseCode);

    /**
     * 创建课程
     * @param course 课程信息
     * @return 是否创建成功
     */
    boolean createCourse(Course course);

    /**
     * 更新课程信息
     * @param course 课程信息
     * @return 是否更新成功
     */
    boolean updateCourse(Course course);

    /**
     * 删除课程
     * @param id 课程ID
     * @return 是否删除成功
     */
    boolean deleteCourse(Long id);

    /**
     * 根据教师ID获取课程列表
     * @param teacherId 教师ID
     * @return 课程列表
     */
    List<Course> getCoursesByTeacherId(Long teacherId);
}
