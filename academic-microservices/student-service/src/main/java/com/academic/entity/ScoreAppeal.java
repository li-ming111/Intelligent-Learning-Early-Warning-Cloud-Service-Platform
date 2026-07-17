package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    private Long studentId;
    private Long scoreId;
    @TableField("warning_id")
    private Long warningId;
    @TableField("course_id")
    private Long courseId;
    private String reason;
    private String attachments;
    private Integer status;
    private String reply;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
