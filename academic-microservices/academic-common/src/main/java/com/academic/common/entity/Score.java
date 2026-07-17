package com.academic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩实体类
 */
@Data
@TableName("scores")
public class Score {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("student_id")
    private Long studentId;
    
    @TableField("course_id")
    private Long courseId;
    
    private String semester;
    
    private Integer year;
    
    @TableField("regular_score")
    private BigDecimal regularScore;
    
    @TableField("final_score")
    private BigDecimal finalScore;
    
    @TableField("score_total")
    private BigDecimal scoreTotal;
    
    private String grade;
    
    @TableField("grade_point")
    private BigDecimal gradePoint;
    
    @TableField("reason_for_change")
    private String reasonForChange;
    
    /** 课程名称（连表查询，非数据库字段） */
    @TableField(exist = false)
    private String courseName;

    /** 学分（连表查询，非数据库字段） */
    @TableField(exist = false)
    private java.math.BigDecimal credits;
    
    @TableField("modified_by")
    private Long modifiedBy;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}