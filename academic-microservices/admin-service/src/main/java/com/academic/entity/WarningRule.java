package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 预警规则实体类
 */
@Data
@Accessors(chain = true)
@TableName("warning_rules")
public class WarningRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String ruleName;

    private String ruleCode;

    private String description;

    @TableField("level")
    private Integer warningLevel;

    @TableField("rule_condition")
    private String expression;

    @TableField("fail_definition")
    private String failDefinition;

    @TableField("statistic_cycle")
    private String statisticCycle;

    @TableField("trigger_type")
    private String triggerType;

    @TableField("release_condition")
    private String releaseCondition;

    @TableField("intervention_plan")
    private String interventionPlan;

    @TableField("notification_flow")
    private String notificationFlow;

    @TableField("responsible_person")
    private String responsiblePerson;

    @TableField("tracking_mechanism")
    private String trackingMechanism;

    @TableField("archive_spec")
    private String archiveSpec;

    @TableField("data_caliber")
    private String dataCaliber;

    @TableField("department_responsibility")
    private String departmentResponsibility;

    @TableField("appeal_process")
    private String appealProcess;

    @TableField("exception_handling")
    private String exceptionHandling;

    private Integer status;

    @TableField("created_by")
    private Long createdBy;

    private LocalDateTime createdAt;

    @TableField("updated_by")
    private Long updatedBy;

    private LocalDateTime updatedAt;
}