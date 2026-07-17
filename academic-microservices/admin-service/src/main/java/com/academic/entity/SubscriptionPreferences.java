package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订阅偏好实体类
 */
@Data
@TableName("subscription_preferences")
public class SubscriptionPreferences {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Integer subscribeWarnings;

    private Integer subscribeLow;

    private Integer subscribeMedium;

    private Integer subscribeHigh;

    private Integer subscribeAssistance;

    private Integer subscribeSystem;

    private Integer pushEmail;

    private Integer pushSms;

    private Integer pushApp;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
