package com.academic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学院实体类
 */
@Data
@TableName("colleges")
public class College {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer status;
    @TableField(exist = false)
    private Integer studentCount;
    @TableField(exist = false)
    private Integer teacherCount;
    @TableField(exist = false)
    private Integer counselorCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}