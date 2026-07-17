package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级实体类
 */
@Data
@TableName("classes")
public class ClassInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long collegeId;
    private Long majorId;
    private Long teacherId;
    private Long counselorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
