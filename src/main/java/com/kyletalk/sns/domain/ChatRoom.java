package com.kyletalk.sns.domain;

import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;
import java.util.List;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChatRoom {
    private String roomId;
    private String roomName;
    private String createdBy;
    private String roomAdmin;
    private String roomPassword;
    private int maxUsers;
    private int currentUsers;
    private Timestamp createdAt;
    private String creatorNickname;
    private String adminNickname;
    private List<ChatMessage> recentMessages;
    
    @Override
    public String toString() {
        return "ChatRoom{" +
                "roomId='" + roomId + '\'' +
                ", roomName='" + roomName + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", roomAdmin='" + roomAdmin + '\'' +
                ", maxUsers=" + maxUsers +
                ", currentUsers=" + currentUsers +
                ", createdAt=" + createdAt +
                '}';
    }
    
    // 비밀번호 설정 여부 확인
    public boolean isPrivate() {
        return roomPassword != null && !roomPassword.isEmpty();
    }

    public void setRecentMessages(List<ChatMessage> recentMessages) {
        this.recentMessages = recentMessages;
    }

    public List<ChatMessage> getRecentMessages() {
        return recentMessages;
    }
} 