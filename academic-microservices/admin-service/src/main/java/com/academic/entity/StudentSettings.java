package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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

    private Long studentId;

    private Integer notificationEnabled;

    private Integer emailNotifications;

    private String theme;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
