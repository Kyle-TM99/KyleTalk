package com.kyletalk.sns.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.kyletalk.sns.domain.NotificationMessage;
import java.util.List;

@Mapper
public interface NotificationMapper {
    // 알림 저장
    void insertNotification(NotificationMessage notification);
    
    // 사용자의 읽지 않은 알림 목록 조회
    List<NotificationMessage> getUnreadNotifications(String memberId);
    
    // 알림 읽음 처리
    void markAsRead(Long notificationId);
    
    // 채팅방 참여자 목록 조회
    List<String> getRoomParticipants(String roomId);
} 