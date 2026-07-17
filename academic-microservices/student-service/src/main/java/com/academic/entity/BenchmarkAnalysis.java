package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 对标分析实体类
 */
@Data
@TableName("benchmark_analysis")
public class BenchmarkAnalysis {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("student_id")
    private Long studentId;
    @TableField("class_id")
    private Long classId;
    @TableField("major_id")
    private Long majorId;
    private String semester;
    @TableField("student_gpa")
    private BigDecimal studentGpa;
    @TableField("class_avg_gpa")
    private BigDecimal classAvgGpa;
    @TableField("major_avg_gpa")
    private BigDecimal majorAvgGpa;
    @TableField("class_rank")
    private Integer classRank;
    @TableField("class_total")
    private Integer classTotal;
    @TableField("major_rank")
    private Integer majorRank;
    @TableField("major_total")
    private Integer majorTotal;
    @TableField("courses_passed")
    private Integer coursesPassed;
    @TableField("courses_failed")
    private Integer coursesFailed;
    @TableField("required_credits")
    private BigDecimal requiredCredits;
    @TableField("credits_on_track")
    private Integer creditsOnTrack;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}