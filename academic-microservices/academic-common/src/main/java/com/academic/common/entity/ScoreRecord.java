package com.academic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("score_record")
public class ScoreRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long studentId;
    
    private Long courseId;
    
    private Long teacherId;
    
    private String studentName;
    
    private String className;
    
    private Double score;
    
    private Integer scoreLevel;
    
    private String remark;
    
    private String semester;
    
    private Integer year;
    
    private Double regularScore;
    
    private Double finalScore;
    
    private Double scoreTotal;
    
    private String grade;
    
    private Double gradePoint;
    
    private String reasonForChange;
    
    private Long modifiedBy;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @TableField(exist = false)
    private String courseName;
    
    @TableField(exist = false)
    private String studentNo;
}