package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 基准分析实体类
 */
@Data
@TableName("benchmark_analysis")
public class BenchmarkAnalysis {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long classId;

    private Long majorId;

    private String semester;

    private BigDecimal studentGpa;

    private BigDecimal classAvgGpa;

    private BigDecimal majorAvgGpa;

    private Integer classRank;

    private Integer classTotal;

    private Integer majorRank;

    private Integer majorTotal;

    private Integer coursesPassed;

    private Integer coursesFailed;

    private BigDecimal requiredCredits;

    private Integer creditsOnTrack;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
