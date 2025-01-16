package com.kyletalk.sns.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ChatMessage {
    private Long messageId;      // 메시지 ID
    private String type;         // 메시지 타입 (CHAT, JOIN, LEAVE)
    private String roomId;       // 채팅방 ID
    private String sender;       // 보내는 사람
    private String nickname;     // 닉네임 필드 추가
    private String message;      // 메시지
    private Timestamp sentAt;    // 전송 시간
    private String socialType;    // 추가
    private String profileImage;  // 추가

    public ChatMessage() {
        // 기본 생성자에서 roomId를 설정하지 않음
    }

    public ChatMessage(String type, String sender, String message) {
        this.type = type;
        this.sender = sender;
        this.message = message;
        this.sentAt = new Timestamp(System.currentTimeMillis());
    }
} 