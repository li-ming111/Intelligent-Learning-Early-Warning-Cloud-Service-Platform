package com.academic.feign;

import com.academic.common.dto.ApiResponse;
import com.academic.common.entity.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Message Service Feign Client
 * 调用 message-service 的接口
 */
@FeignClient(name = "message-service", fallback = MessageServiceFallback.class)
public interface MessageServiceClient {

    /**
     * 获取消息列表
     */
    @GetMapping("/api/messages")
    ApiResponse<List<Message>> getMessages(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "false") Boolean unreadOnly
    );

    /**
     * 发送广播消息
     */
    @PostMapping("/api/messages/broadcast")
    ApiResponse<Map<String, Object>> sendBroadcast(
            @RequestBody Map<String, Object> broadcastData
    );

    /**
     * 发送定向消息
     */
    @PostMapping("/api/messages/send")
    ApiResponse<String> sendMessage(
            @RequestBody Map<String, Object> messageData
    );

    /**
     * 删除用户相关的所有消息
     */
    @DeleteMapping("/api/messages/user/{userId}")
    ApiResponse<String> deleteMessagesByUserId(
            @PathVariable Long userId
    );

    /**
     * 根据ID删除单条消息
     */
    @DeleteMapping("/api/messages/{id}")
    ApiResponse<String> deleteMessageById(
            @PathVariable Long id
    );
}