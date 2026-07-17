package com.academic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 沟通记录实体类
 */
@Data
@TableName("communication_logs")
public class CommunicationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long counselorId;
    private Long teacherId;
    private Long studentId;
    private Long warningId;
    private String content;
    private String category;
    private Integer status;
    private String reply;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}