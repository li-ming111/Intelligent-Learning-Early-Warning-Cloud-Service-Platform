package com.academic.service;

import java.util.Map;

public interface IntelligentWarningService {
    /**
     * 生成智能预警
     */
    Object generateWarning(Map<String, Object> request);

    /**
     * 处理预警
     */
    Object handleWarning(Map<String, Object> request);

    /**
     * 获取预警列表
     */
    Object getWarningList(Map<String, Object> params);

    /**
     * 获取预警详情
     */
    Object getWarningDetail(String warningId);

    /**
     * 更新预警状态
     */
    Object updateWarningStatus(String warningId, Map<String, Object> request);

    /**
     * 根据学生ID获取预警列表
     */
    Object getWarningsByStudentId(Long studentId);

    /**
     * 根据预警等级获取预警列表
     */
    Object getWarningsByLevel(Integer level);
}
