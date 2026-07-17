package com.academic.controller;

import com.academic.common.dto.ApiResponse;
import com.academic.entity.Message;
import com.academic.mapper.UserMapper;
import com.academic.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping({"/api/messages", "/students/messages", "/teachers/messages"})
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserMapper userMapper;

    /** 从 GateWay 注入的头获取请求者 userId */
    private Long getRequestUserId(HttpServletRequest request) {
        String header = request.getHeader("X-User-Id");
        if (header != null) {
            try { return Long.parseLong(header); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    private boolean isAdmin(HttpServletRequest request) {
        String role = request.getHeader("X-Role");
        return "4".equals(role) || "admin".equals(role);
    }

    @GetMapping
    public ApiResponse<List<Message>> getMessages(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "false") Boolean unreadOnly,
            HttpServletRequest request) {
        Long reqUserId = getRequestUserId(request);
        // 非管理员只能查看自己的消息
        if (userId != null && !userId.equals(reqUserId) && !isAdmin(request)) {
            return ApiResponse.error(403, "无权查看其他用户的消息");
        }
        Long effectiveUserId = isAdmin(request) && userId != null ? userId : reqUserId;
        List<Message> messages;
        if (unreadOnly && effectiveUserId != null) {
            messages = messageService.getUnreadMessages(effectiveUserId);
        } else if (effectiveUserId != null) {
            messages = messageService.getMessagesByUserId(effectiveUserId);
        } else {
            messages = messageService.getAllMessages();
        }
        return ApiResponse.success(messages);
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<Message> getMessageById(@PathVariable Long id) {
        Message message = messageService.getMessageById(id);
        if (message != null) {
            return ApiResponse.success(message);
        }
        return ApiResponse.notFound("消息不存在");
    }

    @PostMapping
    public ApiResponse<Message> createMessage(@RequestBody Message message) {
        Message created = messageService.createMessage(message);
        return ApiResponse.success(created);
    }

    @PutMapping("/{id}/read")
    public ApiResponse<String> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return ApiResponse.success("已标记为已读");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ApiResponse.success("消息已删除");
    }
    
    /**
     * 删除用户相关的所有消息（供admin-service调用）
     */
    @DeleteMapping("/user/{userId}")
    public ApiResponse<String> deleteMessagesByUserId(@PathVariable Long userId) {
        log.info("Delete all messages for user: {}", userId);
        messageService.deleteMessagesByUserId(userId);
        return ApiResponse.success("用户消息已删除");
    }

    @GetMapping("/unread/count")
    public ApiResponse<Long> getUnreadCount(@RequestParam Long userId,
                                             HttpServletRequest request) {
        Long reqUserId = getRequestUserId(request);
        if (!userId.equals(reqUserId) && !isAdmin(request)) {
            return ApiResponse.error(403, "无权查看其他用户的未读数");
        }
        Long effectiveUserId = isAdmin(request) ? userId : reqUserId;
        Long count = messageService.getUnreadCount(effectiveUserId);
        return ApiResponse.success(count);
    }

    @PutMapping("/read/all")
    public ApiResponse<String> markAllAsRead(@RequestParam Long userId,
                                              HttpServletRequest request) {
        Long reqUserId = getRequestUserId(request);
        if (!userId.equals(reqUserId) && !isAdmin(request)) {
            return ApiResponse.error(403, "无权操作其他用户的消息");
        }
        Long effectiveUserId = isAdmin(request) ? userId : reqUserId;
        messageService.markAllAsRead(effectiveUserId);
        return ApiResponse.success("所有消息已标记为已读");
    }

    @PostMapping("/send")
    public ApiResponse<String> sendMessage(@RequestBody Map<String, Object> payload) {
        Long userId = null;
        if (payload.containsKey("userId")) {
            userId = ((Number) payload.get("userId")).longValue();
        } else if (payload.containsKey("user_id")) {
            userId = ((Number) payload.get("user_id")).longValue();
        }
        
        String content = (String) payload.get("content");
        
        Integer typeInt = null;
        if (payload.containsKey("type")) {
            Object typeObj = payload.get("type");
            if (typeObj instanceof Integer) {
                typeInt = (Integer) typeObj;
            } else if (typeObj instanceof String) {
                typeInt = Integer.parseInt((String) typeObj);
            }
        }
        
        String typeStr = typeInt != null ? String.valueOf(typeInt) : "0";
        
        messageService.sendMessage(userId, content, typeStr);
        return ApiResponse.success("消息发送成功");
    }

    // ========== 兼容性端点 ==========

    // /teachers/messages/{userId} 和 /students/messages/{userId}
    @GetMapping("/{userId}")
    public ApiResponse<List<Message>> getMessagesByUserId(@PathVariable Long userId,
                                                           HttpServletRequest request) {
        Long reqUserId = getRequestUserId(request);
        if (!userId.equals(reqUserId) && !isAdmin(request)) {
            return ApiResponse.error(403, "无权查看其他用户的消息");
        }
        List<Message> messages = messageService.getMessagesByUserId(userId);
        return ApiResponse.success(messages);
    }

    @GetMapping("/{userId}/unread-count")
    public ApiResponse<Map<String, Object>> getUnreadCountByUserId(@PathVariable Long userId,
                                                                     HttpServletRequest request) {
        Long reqUserId = getRequestUserId(request);
        if (!userId.equals(reqUserId) && !isAdmin(request)) {
            return ApiResponse.error(403, "无权查看其他用户的未读数");
        }
        Long count = messageService.getUnreadCount(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("unreadCount", count);
        result.put("userId", String.valueOf(userId));
        return ApiResponse.success(result);
    }

    // /teachers/messages/{messageId}/mark-read 和 /students/messages/{messageId}/mark-read
    @PostMapping("/{id}/mark-read")
    public ApiResponse<String> markAsReadByPost(@PathVariable Long id) {
        messageService.markAsRead(id);
        return ApiResponse.success("消息已标记为已读");
    }

    // 广播消息端点 - 供 admin-service 调用
    @PostMapping("/broadcast")
    public ApiResponse<Map<String, Object>> sendBroadcast(@RequestBody Map<String, Object> payload) {
        String title = (String) payload.get("title");
        String content = (String) payload.get("content");
        Integer targetRole = payload.get("targetRole") != null ? ((Number) payload.get("targetRole")).intValue() : null;

        int count = 0;
        try {
            // 查询目标用户列表
            List<com.academic.common.entity.User> users;
            if (targetRole != null) {
                users = userMapper.selectList(
                        new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.academic.common.entity.User>()
                                .eq("role", targetRole).eq("status", 1));
            } else {
                users = userMapper.selectList(
                        new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.academic.common.entity.User>()
                                .eq("status", 1));
            }

            // 为每个用户创建消息并写入数据库
            for (com.academic.common.entity.User user : users) {
                Message msg = new Message();
                msg.setReceiverId(user.getId());
                msg.setTitle(title);
                msg.setContent(content);
                msg.setType(1);  // 1=广播通知
                msg.setStatus(0); // 0=未读
                msg.setCreatedAt(LocalDateTime.now());
                messageService.createMessage(msg);
                count++;
            }
            log.info("Broadcast sent to {} users: title={}, targetRole={}", count, title, targetRole);
        } catch (Exception e) {
            log.error("Broadcast failed: {}", e.getMessage(), e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("sentCount", count);
        result.put("message", "Broadcast sent to " + count + " users");
        return ApiResponse.success(result);
    }
}
