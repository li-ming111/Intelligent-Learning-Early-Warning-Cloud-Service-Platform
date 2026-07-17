package com.academic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帮扶评估实体类
 */
@Data
@TableName("assistance_evaluations")
public class AssistanceEvaluation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long planId;
    private Long counselorId;
    private String content;
    private Integer score;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}