package com.kyletalk.sns.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kyletalk.sns.mapper.ChatCalendarMapper;
import com.kyletalk.sns.domain.ChatCalendarEvent;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatCalendarService {
    
    @Autowired
    private ChatCalendarMapper chatCalendarMapper;
    
    public void createEvent(ChatCalendarEvent event) {
        chatCalendarMapper.insertEvent(event);
    }
    
    public List<ChatCalendarEvent> getEventList(String roomId) {
        return chatCalendarMapper.getEventList(roomId);
    }
    
    public ChatCalendarEvent getEvent(Long eventId) {
        return chatCalendarMapper.getEvent(eventId);
    }
    
    public void updateEvent(ChatCalendarEvent event) {
        chatCalendarMapper.updateEvent(event);
    }
    
    public void deleteEvent(Long eventId) {
        chatCalendarMapper.deleteEvent(eventId);
    }
} 