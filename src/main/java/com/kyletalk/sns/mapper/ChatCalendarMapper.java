package com.kyletalk.sns.mapper;

import com.kyletalk.sns.domain.ChatCalendarEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChatCalendarMapper {
    // 일정 추가
    void insertEvent(ChatCalendarEvent event);
    
    // 일정 목록 조회
    List<ChatCalendarEvent> getEventList(@Param("roomId") String roomId);
    
    // 일정 상세 조회
    ChatCalendarEvent getEvent(@Param("eventId") Long eventId);
    
    // 일정 수정
    void updateEvent(ChatCalendarEvent event);
    
    // 일정 삭제
    void deleteEvent(@Param("eventId") Long eventId);
} 