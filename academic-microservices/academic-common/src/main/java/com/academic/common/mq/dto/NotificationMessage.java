package com.academic.common.mq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知消息 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String notificationId;
    private String title;
    private String content;
    private Integer type;  // 1-系统通知, 2-预警通知, 3-成绩通知
    private List<Long> targetUserIds;  // 目标用户ID列表
    private Long senderId;
    private String senderName;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;
}