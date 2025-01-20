package com.kyletalk.sns.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kyletalk.sns.service.ChatCalendarService;
import com.kyletalk.sns.domain.ChatCalendarEvent;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/chat/calendar")
public class ChatCalendarController {
    
    @Autowired
    private ChatCalendarService chatCalendarService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createEvent(@RequestBody ChatCalendarEvent event) {
        Map<String, Object> response = new HashMap<>();
        try {
            chatCalendarService.createEvent(event);
            response.put("success", true);
            response.put("message", "일정이 추가되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{roomId}")
    public ResponseEntity<List<ChatCalendarEvent>> getEventList(@PathVariable String roomId) {
        return ResponseEntity.ok(chatCalendarService.getEventList(roomId));
    }
    
    @GetMapping("/detail/{eventId}")
    public ResponseEntity<ChatCalendarEvent> getEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(chatCalendarService.getEvent(eventId));
    }
    
    @PutMapping("/{eventId}")
    public ResponseEntity<Map<String, Object>> updateEvent(
            @PathVariable Long eventId,
            @RequestBody ChatCalendarEvent event) {
        Map<String, Object> response = new HashMap<>();
        try {
            event.setEventId(eventId);
            chatCalendarService.updateEvent(event);
            response.put("success", true);
            response.put("message", "일정이 수정되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Map<String, Object>> deleteEvent(@PathVariable Long eventId) {
        Map<String, Object> response = new HashMap<>();
        try {
            chatCalendarService.deleteEvent(eventId);
            response.put("success", true);
            response.put("message", "일정이 삭제되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
} 