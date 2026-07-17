package com.academic.controller;

import com.academic.common.dto.ApiResponse;
import com.academic.service.WarningNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 预警通知控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class WarningNotificationController {

    private final WarningNotificationService warningNotificationService;

    /**
     * 发送预警通知
     * @param request 预警通知请求
     */
    @PostMapping("/warning")
    public ApiResponse<Void> sendWarningNotification(@RequestBody WarningNotificationRequest request) {
        log.info("Received warning notification request - Level: {}, StudentId: {}, StudentName: {}",
                request.getWarningLevel(), request.getStudentId(), request.getStudentName());

        try {
            warningNotificationService.sendWarningNotification(
                    request.getWarningLevel(),
                    request.getStudentId(),
                    request.getStudentName(),
                    request.getContent(),
                    request.getExtraData()
            );
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("Failed to send warning notification: {}", e.getMessage());
            return ApiResponse.error("发送预警通知失败: " + e.getMessage());
        }
    }

    /**
     * 获取预警等级与接收者映射信息
     */
    @GetMapping("/level-recipients")
    public ApiResponse<Map<Integer, Object>> getLevelRecipientMapping() {
        Map<Integer, Object> mapping = new HashMap<>();
        
        for (int level = 1; level <= 3; level++) {
            Map<String, Object> levelInfo = new HashMap<>();
            levelInfo.put("description", warningNotificationService.getLevelDescription(level));
            levelInfo.put("recipients", warningNotificationService.getRecipientTypesByLevel(level));
            mapping.put(level, levelInfo);
        }
        
        return ApiResponse.success(mapping);
    }

    /**
     * 预警通知请求DTO
     */
    public static class WarningNotificationRequest {
        private Integer warningLevel;
        private Long studentId;
        private String studentName;
        private String content;
        private Map<String, Object> extraData;

        public Integer getWarningLevel() { return warningLevel; }
        public void setWarningLevel(Integer warningLevel) { this.warningLevel = warningLevel; }
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Map<String, Object> getExtraData() { return extraData; }
        public void setExtraData(Map<String, Object> extraData) { this.extraData = extraData; }
    }
}