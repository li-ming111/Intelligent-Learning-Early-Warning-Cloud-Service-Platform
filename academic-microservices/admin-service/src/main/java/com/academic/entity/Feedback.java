package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈实体类
 */
@Data
@TableName("feedbacks")
public class Feedback {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teacherId;

    private Long studentId;

    private String content;

    private String category;

    private Integer rating;

    private String replyContent;

    private LocalDateTime repliedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String status;
}
