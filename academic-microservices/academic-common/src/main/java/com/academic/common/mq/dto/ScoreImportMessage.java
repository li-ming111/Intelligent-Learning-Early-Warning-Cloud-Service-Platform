package com.academic.common.mq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 成绩导入消息 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreImportMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String batchId;
    private List<Map<String, Object>> data;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime createTime;
}