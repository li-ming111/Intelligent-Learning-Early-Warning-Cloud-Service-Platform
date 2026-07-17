package com.academic.service;

import java.util.Map;

public interface AIService {
    /**
     * 分析学生成绩 - 计算统计指标、识别薄弱科目
     */
    Object analyzeScore(Map<String, Object> request);

    /**
     * 生成个性化学习建议
     */
    Object generateSuggestion(Map<String, Object> request);

    /**
     * 分析学生成绩趋势
     */
    Object analyzeBehavior(Map<String, Object> request);

    /**
     * 预测学业风险等级
     */
    Object predictRisk(Map<String, Object> request);

    /**
     * 基于风险等级生成预警建议
     */
    Object generateWarning(Map<String, Object> request);
}
