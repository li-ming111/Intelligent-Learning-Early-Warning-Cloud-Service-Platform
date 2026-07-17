package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 选课记录实体类
 */
@Data
@TableName("enrollments")
public class Enrollment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long courseId;
    private Integer status;
    private Integer semester;
    private Integer year;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
