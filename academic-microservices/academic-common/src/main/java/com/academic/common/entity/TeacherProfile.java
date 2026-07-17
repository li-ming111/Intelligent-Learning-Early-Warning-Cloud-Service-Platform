package com.academic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教师信息实体类
 */
@Data
@TableName("teacher_profile")
public class TeacherProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private Long collegeId;
    private String title;
    private String researchAreas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}