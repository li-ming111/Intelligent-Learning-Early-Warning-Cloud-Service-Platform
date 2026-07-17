package com.academic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("college_admin_profile")
public class CollegeAdminProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private Long collegeId;
    private String phone;
    private String email;
    private String title;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
}
