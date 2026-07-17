package com.academic.service.impl;

import com.academic.entity.Message;
import com.academic.entity.Notification;
import com.academic.service.MessageService;
import com.academic.service.WarningNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 预警通知服务实现类
 * 
 * 预警等级与接收者映射：
 * - 低级预警: 教师、学生
 * - 中级预警: 教师、学生、辅导员
 * - 高级预警: 教师、学生、辅导员、管理员
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WarningNotificationServiceImpl implements WarningNotificationService {

    private final MessageService messageService;

    /**
     * 预警等级与接收者类型映射
     * key: 预警等级 (1-低级, 2-中级, 3-高级)
     * value: 接收者类型数组
     */
    private static final Map<Integer, String[]> LEVEL_RECIPIENT_MAP = new HashMap<>();

    /**
     * 预警等级描述映射
     */
    private static final Map<Integer, String> LEVEL_DESCRIPTION_MAP = new HashMap<>();

    static {
        // 初始化预警等级与接收者映射
        LEVEL_RECIPIENT_MAP.put(1, new String[]{"teacher", "student"});      // 低级预警
        LEVEL_RECIPIENT_MAP.put(2, new String[]{"teacher", "student", "counselor"});  // 中级预警
        LEVEL_RECIPIENT_MAP.put(3, new String[]{"teacher", "student", "counselor", "admin"});  // 高级预警

        // 初始化预警等级描述
        LEVEL_DESCRIPTION_MAP.put(1, "低级预警");
        LEVEL_DESCRIPTION_MAP.put(2, "中级预警");
        LEVEL_DESCRIPTION_MAP.put(3, "高级预警");
    }

    @Override
    public void sendWarningNotification(Integer warningLevel, Long studentId, String studentName, 
                                        String content, Map<String, Object> extraData) {
        log.info("Sending warning notification - Level: {}, StudentId: {}, StudentName: {}", 
                warningLevel, studentId, studentName);

        String[] recipientTypes = getRecipientTypesByLevel(warningLevel);
        if (recipientTypes == null || recipientTypes.length == 0) {
            log.warn("No recipients defined for warning level: {}", warningLevel);
            return;
        }

        String levelDesc = getLevelDescription(warningLevel);
        String title = String.format("[%s] 学业预警通知", levelDesc);

        List<Message> messages = new ArrayList<>();

        // 根据接收者类型发送通知
        for (String recipientType : recipientTypes) {
            List<Long> recipientIds = getRecipientIdsByType(recipientType, studentId, extraData);
            
            for (Long recipientId : recipientIds) {
                Message message = createNotificationMessage(studentId, studentName, recipientId, 
                        recipientType, warningLevel, title, content, extraData);
                messages.add(message);
            }

            // 记录发送日志
            logNotification(warningLevel, levelDesc, recipientType, recipientIds.size(), studentId, studentName);
        }

        // 批量保存消息
        if (!messages.isEmpty()) {
            messageService.saveBatch(messages);
            log.info("Successfully sent {} warning notification messages for student {}", 
                    messages.size(), studentName);
        }
    }

    @Override
    public String[] getRecipientTypesByLevel(Integer warningLevel) {
        return LEVEL_RECIPIENT_MAP.getOrDefault(warningLevel, new String[0]);
    }

    @Override
    public String getLevelDescription(Integer warningLevel) {
        return LEVEL_DESCRIPTION_MAP.getOrDefault(warningLevel, "未知预警");
    }

    /**
     * 根据接收者类型获取接收者ID列表
     * 这里模拟获取接收者ID，实际应从数据库查询
     */
    private List<Long> getRecipientIdsByType(String recipientType, Long studentId, Map<String, Object> extraData) {
        List<Long> recipientIds = new ArrayList<>();

        switch (recipientType) {
            case "student":
                // 学生本人
                recipientIds.add(studentId);
                break;
            case "teacher":
                // 获取学生的任课教师ID（从额外数据中获取或默认教师ID）
                if (extraData != null && extraData.containsKey("teacherId")) {
                    recipientIds.add((Long) extraData.get("teacherId"));
                } else {
                    // 默认教师ID
                    recipientIds.add(2L); // 教师用户ID
                }
                break;
            case "counselor":
                // 获取学生的辅导员ID（从额外数据中获取或默认辅导员ID）
                if (extraData != null && extraData.containsKey("counselorId")) {
                    recipientIds.add((Long) extraData.get("counselorId"));
                } else {
                    // 默认辅导员ID
                    recipientIds.add(4L); // 辅导员用户ID
                }
                break;
            case "admin":
                // 获取管理员ID（系统管理员）
                recipientIds.add(1L); // 管理员用户ID
                break;
            default:
                log.warn("Unknown recipient type: {}", recipientType);
        }

        return recipientIds;
    }

    /**
     * 创建通知消息对象
     */
    private Message createNotificationMessage(Long studentId, String studentName, Long recipientId,
                                              String recipientType, Integer warningLevel,
                                              String title, String content, Map<String, Object> extraData) {
        Message message = new Message();
        message.setSenderId(0L); // 系统发送
        message.setReceiverId(recipientId);
        message.setContent(content);
        message.setType(3); // 预警通知类型
        message.setStatus(0); // 未读
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        
        // 设置消息标题（存储在extra字段）
        if (extraData != null) {
            message.setExtra(String.format("{\"title\":\"%s\",\"warningLevel\":%d,\"studentId\":%d,\"studentName\":\"%s\",\"recipientType\":\"%s\"}",
                    title, warningLevel, studentId, studentName, recipientType));
        } else {
            message.setExtra(String.format("{\"title\":\"%s\",\"warningLevel\":%d,\"studentId\":%d,\"studentName\":\"%s\",\"recipientType\":\"%s\"}",
                    title, warningLevel, studentId, studentName, recipientType));
        }

        return message;
    }

    /**
     * 记录预警通知日志
     */
    private void logNotification(Integer warningLevel, String levelDesc, String recipientType, 
                                  int recipientCount, Long studentId, String studentName) {
        log.info("预警通知发送记录 - 预警等级: {}, 等级描述: {}, 接收者类型: {}, 接收者数量: {}, 学生ID: {}, 学生姓名: {}, 发送时间: {}",
                warningLevel, levelDesc, recipientType, recipientCount, studentId, studentName, LocalDateTime.now());
    }
}