package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("student_profile")
public class StudentProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String studentId;
    private String name;
    private String gender;
    private Long collegeId;
    private Long majorId;
    private Long classId;
    private String className;
    private Integer enrollmentYear;
    private Integer privacyLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
