package com.kyletalk.sns.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.kyletalk.sns.domain.NotificationMessage;
import com.kyletalk.sns.mapper.NotificationMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/notifications")
@Slf4j
public class NotificationController {
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    // 읽지 않은 알림 목록 조회
    @GetMapping("/unread/{memberId}")
    public List<NotificationMessage> getUnreadNotifications(@PathVariable String memberId) {
        log.info("Fetching unread notifications for member: {}", memberId);
        List<NotificationMessage> notifications = notificationMapper.getUnreadNotifications(memberId);
        log.info("Found {} unread notifications", notifications.size());
        return notifications;
    }
    
    // 알림 읽음 처리
    @PostMapping("/{notificationId}/read")
    public void markAsRead(@PathVariable Long notificationId) {
        log.info("Marking notification as read: {}", notificationId);
        notificationMapper.markAsRead(notificationId);
    }
} 