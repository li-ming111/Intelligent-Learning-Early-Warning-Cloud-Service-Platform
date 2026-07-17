package com.academic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学术预警实体类
 */
@Data
@Accessors(chain = true)
@TableName("academic_warning")
public class AcademicWarning {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long studentId;
    
    private String studentName;
    
    private Long ruleId;
    
    // collegeId 在 academic_warning 表中不存在（仅用于 service 层临时传递）
    @TableField(exist = false)
    private Long collegeId;
    
    private Integer type;
    
    private String title;
    
    private String description;
    
    @TableField("warning_level")
    private Integer warningLevel;
    
    private String warningMessage;
    
    private BigDecimal triggerScore;
    
    private BigDecimal gpaBefore;
    
    private BigDecimal gpaAfter;
    
    private Integer status;
    
    private String appealStatus;
    
    private String handledBy;
    
    private String handleResult;
    
    private LocalDateTime handledAt;
    
    private String createdBy;
    
    private LocalDateTime createdAt;
    
    private String updatedBy;
    
    private LocalDateTime updatedAt;
}