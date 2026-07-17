package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级实体类
 */
@Data
@TableName("classes")
public class Classes {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    @TableField("college_id")
    private Long collegeId;
    @TableField("teacher_id")
    private Long teacherId;
    @TableField("counselor_id")
    private Long counselorId;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
    @TableField("major_id")
    private Long majorId;
}
