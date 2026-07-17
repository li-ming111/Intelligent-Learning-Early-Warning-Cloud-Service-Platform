package com.academic.common.mq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 导入结果消息 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String batchId;
    private String type;  // user-用户导入, score-成绩导入
    private Integer successCount;
    private Integer failCount;
    private String message;
    private String fileUrl;  // 导出文件URL（如果生成）
    private LocalDateTime finishTime;
}