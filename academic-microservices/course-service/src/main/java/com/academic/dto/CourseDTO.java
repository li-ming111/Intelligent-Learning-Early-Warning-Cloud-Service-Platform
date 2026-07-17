package com.academic.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程数据传输对象
 */
@Data
public class CourseDTO {
    private Long id;
    private String code;
    private String name;
    private BigDecimal credits;
    private String type;
    private Long moduleId;
    private String scoreTemplate;
    private Long teacherId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
