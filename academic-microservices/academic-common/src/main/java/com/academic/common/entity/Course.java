package com.academic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程实体类
 */
@Data
@TableName("courses")
public class Course {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String name;
    private BigDecimal credits;
    private String type;
    private String description;
    @TableField("module_id")
    private Long moduleId;
    @TableField("score_template")
    private String scoreTemplate;
    @TableField("teacher_id")
    private Long teacherId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}