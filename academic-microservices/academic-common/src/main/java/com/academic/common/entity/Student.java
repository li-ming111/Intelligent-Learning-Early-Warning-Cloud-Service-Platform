package com.academic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生实体类
 */
@Data
@TableName("student_profile")
public class Student {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("student_id")
    private String studentId;
    private String name;
    @TableField("college_id")
    private Long collegeId;
    @TableField("major_id")
    private Long majorId;
    @TableField("class_id")
    private Long classId;
    @TableField("privacy_level")
    private Integer privacyLevel;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
    
    @TableField(exist = false)
    private String className;
    @TableField(exist = false)
    private String email;
    @TableField(exist = false)
    private String phone;
}