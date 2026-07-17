package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订阅偏好实体类
 */
@Data
@TableName("subscription_preferences")
public class SubscriptionPreference {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("student_id")
    private Long studentId;
    @TableField("subscribe_warnings")
    private Integer subscribeWarnings;
    @TableField("subscribe_low")
    private Integer subscribeLow;
    @TableField("subscribe_medium")
    private Integer subscribeMedium;
    @TableField("subscribe_high")
    private Integer subscribeHigh;
    @TableField("subscribe_assistance")
    private Integer subscribeAssistance;
    @TableField("subscribe_system")
    private Integer subscribeSystem;
    @TableField("push_email")
    private Integer pushEmail;
    @TableField("push_sms")
    private Integer pushSms;
    @TableField("push_app")
    private Integer pushApp;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}