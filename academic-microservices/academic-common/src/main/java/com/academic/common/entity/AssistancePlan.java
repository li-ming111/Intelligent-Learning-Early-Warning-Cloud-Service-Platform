package com.academic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帮扶计划实体类
 */
@Data
@TableName("assistance_plans")
public class AssistancePlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long counselorId;
    private Long studentId;
    private String content;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}