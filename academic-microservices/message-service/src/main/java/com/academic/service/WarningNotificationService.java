package com.academic.service;

import java.util.Map;

/**
 * 预警通知服务接口
 */
public interface WarningNotificationService {

    /**
     * 发送预警通知
     * @param warningLevel 预警等级: 1-低级, 2-中级, 3-高级
     * @param studentId 学生ID
     * @param studentName 学生姓名
     * @param content 预警内容
     * @param extraData 额外数据（如挂科课程列表等）
     */
    void sendWarningNotification(Integer warningLevel, Long studentId, String studentName, String content, Map<String, Object> extraData);

    /**
     * 根据预警等级获取接收者类型列表
     * @param warningLevel 预警等级
     * @return 接收者类型列表
     */
    String[] getRecipientTypesByLevel(Integer warningLevel);

    /**
     * 获取预警等级描述
     * @param warningLevel 预警等级
     * @return 预警等级描述
     */
    String getLevelDescription(Integer warningLevel);
}