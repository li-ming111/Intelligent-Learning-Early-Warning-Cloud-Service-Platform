package com.academic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 成绩申诉实体类
 */
@Data
@TableName("score_appeals")
public class ScoreAppeal {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long warningId;
    private Long studentId;
    private Long scoreId;
    private Long courseId;
    private String reason;
    private String attachments;
    private Integer status;
    private String reply;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}