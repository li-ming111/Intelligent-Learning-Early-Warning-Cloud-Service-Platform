package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("messages")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String title;
    private String content;
    private Integer type;
    private Integer status;
    private String extra;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime readAt;
    
    @TableField(exist = false)
    private String senderName;
}
