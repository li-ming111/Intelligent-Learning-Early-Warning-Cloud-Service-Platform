package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 选修模块实体类
 */
@Data
@TableName("elective_modules")
public class ElectiveModule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String moduleCode;

    private String moduleName;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
