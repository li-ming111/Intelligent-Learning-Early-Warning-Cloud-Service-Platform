package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 导出历史实体类
 */
@Data
@TableName("export_history")
public class ExportHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String dataType;

    private String fileName;

    private Integer recordCount;

    private Long exportedBy;

    private LocalDateTime createdAt;
}
