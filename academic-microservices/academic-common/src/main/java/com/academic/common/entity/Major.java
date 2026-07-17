package com.academic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 专业实体类
 */
@Data
@TableName("majors")
public class Major {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long collegeId;
    private String name;
    private String code;
    @TableField("short_name")
    private String shortName;
    private String description;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}