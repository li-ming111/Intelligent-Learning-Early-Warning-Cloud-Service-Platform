package com.academic.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket 消息控制器
 * 处理实时消息推送
 */
@Slf4j
@RestController
@RequestMapping("/ws")
public class WebSocketMessageController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketMessageController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 广播消息到所有订阅者
     */
    @MessageMapping("/broadcast")
    @SendTo("/topic/messages")
    public Map<String, Object> broadcastMessage(Map<String, Object> message) {
        log.info("Broadcast message: {}", message);
        return message;
    }

    /**
     * 发送消息到指定用户
     * @param payload 包含 userId 和 message 的 Map
     */
    @MessageMapping("/sendToUser")
    public void sendToUser(@RequestBody Map<String, Object> payload) {
        Long userId = ((Number) payload.get("userId")).longValue();
        Map<String, Object> message = (Map<String, Object>) payload.get("message");
        
        log.info("Send message to user {}: {}", userId, message);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId), 
                "/queue/messages", 
                message
        );
    }

    /**
     * 通过 REST API 发送消息到指定用户
     * 供其他服务调用
     */
    @PostMapping("/send")
    public Map<String, Object> sendMessageToUser(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = ((Number) payload.get("userId")).longValue();
            Map<String, Object> message = (Map<String, Object>) payload.get("message");
            
            log.info("REST API send message to user {}: {}", userId, message);
            
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(userId), 
                    "/queue/messages", 
                    message
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "消息已发送");
            return response;
        } catch (Exception e) {
            log.error("Send message failed: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }

    /**
     * 通过 REST API 广播消息
     */
    @PostMapping("/broadcast")
    public Map<String, Object> broadcastMessageViaRest(@RequestBody Map<String, Object> message) {
        try {
            log.info("REST API broadcast message: {}", message);
            messagingTemplate.convertAndSend("/topic/messages", message);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "消息已广播");
            return response;
        } catch (Exception e) {
            log.error("Broadcast message failed: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }
}
