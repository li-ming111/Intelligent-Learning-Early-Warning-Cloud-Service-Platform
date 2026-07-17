package com.academic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教师课程关联实体类
 */
@Data
@TableName("teacher_course")
public class TeacherCourse {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long teacherId;
    private Long courseId;
    private Integer semester;
    private Integer year;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}