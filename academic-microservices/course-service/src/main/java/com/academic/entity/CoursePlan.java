package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 课程计划实体类
 */
@Data
@TableName("course_plans")
public class CoursePlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private Long teacherId;
    private String semester;
    private Integer year;
    private LocalDate startDate;
    private LocalDate endDate;
    private String classTime;
    private String location;
    private Integer maxStudents;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
