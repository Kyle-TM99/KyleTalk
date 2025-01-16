package com.kyletalk.sns.controller;

import com.kyletalk.sns.domain.ChatMessage;
import com.kyletalk.sns.domain.ChatRoom;
import com.kyletalk.sns.domain.Member;
import com.kyletalk.sns.mapper.ChatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;

@Controller
@Slf4j
public class ChatController {

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/chat/delete/{roomId}")
    public String deleteChatRoom(@PathVariable("roomId") String roomId) {
        chatMapper.deleteChatRoom(roomId);
        return "redirect:/chat";
    }

    // 채팅방 목록 페이지
    @GetMapping("/chat")
    public String chatRooms(Model model, HttpSession session) {
        if(session.getAttribute("member") == null) {
            return "redirect:/loginForm";
        }
        
        List<ChatRoom> chatRooms = chatMapper.getAllChatRooms();
        model.addAttribute("chatRooms", chatRooms);
        return "views/chatRooms";
    }
    
    // 특정 채팅방 입장
    @GetMapping("/chat/{roomId}")
    public String chatRoom(@PathVariable("roomId") String roomId, Model model, HttpSession session) {
        if(session.getAttribute("member") == null) {
            return "redirect:/loginForm";
        }
        
        ChatRoom room = chatMapper.getChatRoomById(roomId);
        if(room == null) {
            return "redirect:/chat";
        }
        
        Member member = (Member) session.getAttribute("member");
        
        // 이미 참여 중인지 확인
        if(!chatMapper.isParticipant(roomId, member.getMemberId())) {
            chatMapper.addParticipant(roomId, member.getMemberId());
            // 현재 참여자 수를 가져와서 1 증가
            int currentCount = chatMapper.getParticipantCount(roomId);
            chatMapper.updateParticipantCount(roomId, currentCount);
        }
        
        // 현재 참여자 수를 모델에 추가
        int participantCount = chatMapper.getParticipantCount(roomId);
        model.addAttribute("participantCount", participantCount);
        
        List<ChatMessage> recentMessages = chatMapper.getRecentMessages(roomId, 50);
        model.addAttribute("room", room);
        model.addAttribute("recentMessages", recentMessages);
        return "views/chat";
    }
    
    // 채팅방 생성 API
    @PostMapping("/api/chat/rooms")
    @ResponseBody
    public Map<String, Object> createRoom(@RequestBody ChatRoom chatRoom, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Member member = (Member) session.getAttribute("member");
            if (member == null) {
                response.put("success", false);
                response.put("error", "로그인이 필요합니다.");
                return response;
            }
            
            // 채팅방 기본 정보 설정
            chatRoom.setRoomId(UUID.randomUUID().toString());
            chatRoom.setCreatedBy(member.getMemberId());
            chatRoom.setRoomAdmin(member.getMemberId());  // 방장 설정
            chatRoom.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            chatRoom.setCurrentUsers(0);
            
            // 최대 인원수가 설정되지 않은 경우 기본값 설정
            if (chatRoom.getMaxUsers() <= 0) {
                chatRoom.setMaxUsers(100);
            }
            
            log.info("Creating chat room: {}", chatRoom);  // 로그 추가
            
            // 채팅방 생성
            chatMapper.createChatRoom(chatRoom);
            
            response.put("success", true);
            response.put("roomId", chatRoom.getRoomId());
            
        } catch (Exception e) {
            log.error("채팅방 생성 실패: ", e);
            response.put("success", false);
            response.put("error", "채팅방 생성에 실패했습니다: " + e.getMessage());
        }
        
        return response;
    }
    
    // 비밀번호 확인 API
    @PostMapping("/api/chat/rooms/{roomId}/verify")
    @ResponseBody
    public Map<String, Object> verifyPassword(@PathVariable("roomId") String roomId, 
                                            @RequestBody Map<String, String> request) {
        String password = request.get("password");
        String storedPassword = chatMapper.getRoomPassword(roomId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", password != null && password.equals(storedPassword));
        return response;
    }
    
    // WebSocket 메시지 처리
    @MessageMapping("/chat/room/{roomId}/sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, 
                           @DestinationVariable("roomId") String roomId) {
        try {
            // 채팅방이 존재하는지 먼저 확인
            ChatRoom room = chatMapper.getChatRoomById(roomId);
            if (room != null) {
                chatMessage.setRoomId(roomId);
                chatMessage.setSentAt(new Timestamp(System.currentTimeMillis()));
                chatMapper.insertMessage(chatMessage);
                
                // 특정 채팅방으로만 메시지 전송
                messagingTemplate.convertAndSend("/topic/room/" + roomId, chatMessage);
            } else {
                log.error("Chat room not found: {}", roomId);
            }
        } catch (Exception e) {
            log.error("Error in sendMessage: ", e);
        }
    }

    @MessageMapping("/chat/room/{roomId}/addUser")
    public void addUser(@Payload ChatMessage chatMessage,
                       SimpMessageHeaderAccessor headerAccessor,
                       @DestinationVariable("roomId") String roomId) {
        try {
            // 채팅방이 존재하는지 먼저 확인
            ChatRoom room = chatMapper.getChatRoomById(roomId);
            if (room != null) {
                chatMessage.setRoomId(roomId);
                
                Member member = (Member) headerAccessor.getSessionAttributes().get("member");
                if (member != null) {
                    chatMessage.setNickname(member.getNickname());
                }
                
                headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
                chatMessage.setSentAt(new Timestamp(System.currentTimeMillis()));
                chatMapper.insertMessage(chatMessage);
                
                // 특정 채팅방으로만 입장 메시지 전송
                messagingTemplate.convertAndSend("/topic/room/" + roomId, chatMessage);
            } else {
                log.error("Chat room not found: {}", roomId);
            }
        } catch (Exception e) {
            log.error("Error in addUser: ", e);
        }
    }

    @PostMapping("/api/chat/rooms/{roomId}/transfer-admin")
    @ResponseBody
    public Map<String, Object> transferAdmin(@PathVariable("roomId") String roomId,
                                       @RequestBody Map<String, String> request,
                                       HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Member currentMember = (Member) session.getAttribute("member");
        String newAdminId = request.get("newAdminId");
        
        try {
            // 현재 사용자가 방장인지 확인
            if (!chatMapper.isRoomAdmin(roomId, currentMember.getMemberId())) {
                response.put("success", false);
                response.put("error", "방장 권한이 없습니다.");
                return response;
            }
            
            // 권한 양도
            chatMapper.transferRoomAdmin(roomId, currentMember.getMemberId(), newAdminId);
            response.put("success", true);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }

    // 채팅방 참여자 목록 조회 API
    @GetMapping("/api/chat/rooms/{roomId}/participants")
    @ResponseBody
    public Map<String, Object> getRoomParticipants(@PathVariable("roomId") String roomId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Member> participants = chatMapper.getRoomParticipants(roomId);
            response.put("success", true);
            response.put("participants", participants);
            response.put("count", participants.size());
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "참여자 목록을 불러오는데 실패했습니다.");
        }
        return response;
    }

    @PostMapping("/api/chat/rooms/{roomId}/leave")
    @ResponseBody
    public Map<String, Object> leaveRoom(@PathVariable("roomId") String roomId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        log.info("Received leave request for room: {}", roomId);  // 추가된 로그
        
        try {
            Member member = (Member) session.getAttribute("member");
            log.info("Current member from session: {}", member);  // 추가된 로그
            
            if (member != null) {
                log.info("Attempting to remove participant: memberId={}, roomId={}", member.getMemberId(), roomId);
                
                // 참여자인지 먼저 확인
                boolean isParticipant = chatMapper.isParticipant(roomId, member.getMemberId());
                if (!isParticipant) {
                    response.put("success", false);
                    response.put("error", "해당 채팅방의 참여자가 아닙니다.");
                    return response;
                }

                // 방장인 경우 체크
                boolean isAdmin = chatMapper.isRoomAdmin(roomId, member.getMemberId());
                if (isAdmin) {
                    response.put("success", false);
                    response.put("error", "방장은 방을 나갈 수 없습니다. 먼저 방장을 위임해주세요.");
                    return response;
                }
                
                // 참여자 제거
                chatMapper.removeParticipant(roomId, member.getMemberId());
                log.info("Successfully removed participant");
                
                // 현재 참여자 수를 가져와서 업데이트
                int currentCount = chatMapper.getParticipantCount(roomId);
                
                // 마지막 참여자였다면 채팅방 삭제
                if (currentCount == 0) {
                    chatMapper.deleteChatRoom(roomId);
                    log.info("Chat room deleted as it was empty: {}", roomId);
                } else {
                    chatMapper.updateParticipantCount(roomId, currentCount);
                    log.info("Updated participant count to: {}", currentCount);
                }
                
                response.put("success", true);
                response.put("message", "성공적으로 채팅방을 나갔습니다.");
            } else {
                log.error("Member not found in session");
                response.put("success", false);
                response.put("error", "로그인이 필요합니다.");
            }
        } catch (Exception e) {
            log.error("Error while leaving chat room: ", e);
            response.put("success", false);
            response.put("error", "채팅방을 나가는 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        log.info("Leave room response: {}", response);  // 추가된 로그
        return response;
    }
} 