package com.kyletalk.sns.domain;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationMessage {
    private Long notificationId;    // 알림 고유 ID
    private String memberId;        // 알림을 받을 사용자 ID
    private String roomId;          // 채팅방 ID
    private String message;         // 알림 메시지 내용
    private String sender;          // 발신자 ID
    private String senderNickname;  // 발신자 닉네임
    private String type;            // 알림 타입 (CHAT, BOARD, CALENDAR)
    private boolean isRead;         // 읽음 여부
    private LocalDateTime createdAt; // 생성 시간
} 