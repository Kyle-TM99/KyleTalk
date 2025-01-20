package com.kyletalk.sns.domain;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChatCalendarEvent {
    private Long eventId;
    private String roomId;
    private String createdBy;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean allDay;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 추가 필드: 생성자 정보
    private String creatorNickname;
    private String creatorImage;
    
    // FullCalendar와의 호환을 위한 메서드
    public boolean getStart() {
        return startDate != null;
    }
    
    public boolean getEnd() {
        return endDate != null;
    }
} 