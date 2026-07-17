package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生设置实体类
 */
@Data
@TableName("student_settings")
public class StudentSettings {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("student_id")
    private Long studentId;
    @TableField("notification_enabled")
    private Integer notificationEnabled;
    @TableField("email_notifications")
    private Integer emailNotifications;
    private String theme;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}