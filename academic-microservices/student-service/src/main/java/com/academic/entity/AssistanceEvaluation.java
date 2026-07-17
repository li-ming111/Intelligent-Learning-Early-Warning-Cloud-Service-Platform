package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帮扶评价实体类
 */
@Data
@TableName("assistance_evaluations")
public class AssistanceEvaluation {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("plan_id")
    private Long planId;
    @TableField("counselor_id")
    private Long counselorId;
    private String content;
    private Integer score;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}