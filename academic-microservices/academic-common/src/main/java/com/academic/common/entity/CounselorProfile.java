package com.academic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 辅导员信息实体类
 */
@Data
@TableName("counselor_profiles")
public class CounselorProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long collegeId;
    private String counselorId;
    private String name;
    private String gender;
    private String phone;
    private String email;
    private String department;
    private String title;
    private String education;
    private String introduction;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 新增字段，根据设计文档
    private String collegeName;
    private String staffId;
    private String officeLocation;
    private String dutyPhone;
    private Integer classCount;
    private Integer studentCount;
}