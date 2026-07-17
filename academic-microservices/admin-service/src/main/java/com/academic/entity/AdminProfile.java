package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员个人资料实体类
 */
@Data
@TableName("admin_profile")
public class AdminProfile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String name;

    private String phone;

    private String email;

    private String department;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
