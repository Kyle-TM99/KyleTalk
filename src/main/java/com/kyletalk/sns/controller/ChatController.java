package com.kyletalk.sns.controller;

import com.kyletalk.sns.domain.ChatMessage;
import com.kyletalk.sns.domain.ChatRoom;
import com.kyletalk.sns.domain.Member;
import com.kyletalk.sns.service.ChatService;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import jakarta.servlet.http.HttpSession;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;

@Controller
@Slf4j
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 채팅방 삭제
    @PostMapping("/chat/delete/{roomId}")
    @ResponseBody
    public Map<String, Object> deleteChatRoom(@PathVariable("roomId") String roomId) {
        try {
            chatService.deleteChatRoom(roomId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "채팅방이 삭제되었습니다.");
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }

    // 채팅방 목록 페이지
    @GetMapping("/chat")
    public String chatRooms(Model model, HttpSession session) {
        if(session.getAttribute("member") == null) {
            return "redirect:/loginForm";
        }
        
        List<ChatRoom> chatRooms = chatService.getAllChatRooms();
        model.addAttribute("chatRooms", chatRooms);
        return "views/chatRooms";
    }
    
    // 특정 채팅방 입장
    @GetMapping("/chat/{roomId}")
    public String chatRoom(@PathVariable("roomId") String roomId, Model model, HttpSession session) {
        if(session.getAttribute("member") == null) {
            return "redirect:/loginForm";
        }
        
        try {
            ChatRoom room = chatService.findRoom(roomId);
            List<ChatMessage> messages = chatService.getMessages(roomId);  // 채팅 메시지 조회
            
            model.addAttribute("room", room);
            model.addAttribute("messages", messages);  // 메시지를 모델에 추가
            
            return "views/chat";
        } catch (Exception e) {
            log.error("채팅방 입장 실패: {}", e.getMessage());
            return "redirect:/chat";
        }
    }
    
    // 채팅방 생성 API
    @PostMapping("/api/chat/rooms")
    @ResponseBody
    public Map<String, Object> createRoom(@RequestBody ChatRoom room, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Member member = (Member) session.getAttribute("member");
            if (member == null) {
                throw new RuntimeException("로그인이 필요합니다.");
            }
            
            String roomId = chatService.createRoom(room, member);
            
            response.put("success", true);
            response.put("roomId", roomId);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }
    
    // 비밀번호 확인 API
    @PostMapping("/api/chat/rooms/{roomId}/verify")
    @ResponseBody
    public Map<String, Object> verifyRoomPassword(
            @PathVariable("roomId") String roomId,
            @RequestBody Map<String, String> request,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Member member = (Member) session.getAttribute("member");
            if (member == null) {
                throw new RuntimeException("로그인이 필요합니다.");
            }

            chatService.verifyAndJoinRoom(roomId, request.get("password"), member);
            
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }
    
    // WebSocket 메시지 처리
    @MessageMapping("/chat/{roomId}/sendMessage")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage sendMessage(@DestinationVariable("roomId") String roomId, ChatMessage message) {
        message.setType("CHAT");
        message.setSentAt(new Timestamp(System.currentTimeMillis()));
        chatService.insertMessage(message);
        return message;
    }

    // 채팅방 참여
    @MessageMapping("/chat/{roomId}/join")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage join(@DestinationVariable("roomId") String roomId, ChatMessage message) {
        try {
            // 참여자 추가
            if (!chatService.isParticipant(roomId, message.getSender())) {
                chatService.addParticipant(roomId, message.getSender());
            }
            
            // 참여자 목록 업데이트
            List<Member> participants = chatService.getRoomParticipants(roomId);
            messagingTemplate.convertAndSend("/topic/chat/" + roomId + "/participants", participants);
            
            message.setMessage(message.getNickname() + "님이 입장하셨습니다.");
            return message;
        } catch (Exception e) {
            log.error("Error in join: ", e);
            throw new RuntimeException("채팅방 참여 중 오류가 발생했습니다.");
        }
    }

    // 채팅방 참여자 추가
    @MessageMapping("/chat/room/{roomId}/addUser")
    public void addUser(@Payload ChatMessage chatMessage,
                       SimpMessageHeaderAccessor headerAccessor,
                       @DestinationVariable("roomId") String roomId) {
        try {
            ChatRoom room = chatService.findRoom(roomId);
            chatMessage.setRoomId(roomId);
            
            Member member = (Member) headerAccessor.getSessionAttributes().get("member");
            if (member != null) {
                chatMessage.setNickname(member.getNickname());
                chatMessage.setProfileImage(member.getMemberImage());
            }
            
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
            chatMessage.setSentAt(new Timestamp(System.currentTimeMillis()));
            chatService.insertMessage(chatMessage);
            
            messagingTemplate.convertAndSend("/topic/room/" + roomId, chatMessage);
        } catch (Exception e) {
            log.error("Error in addUser: ", e);
            throw new RuntimeException("사용자 추가 중 오류가 발생했습니다.");
        }
    }

    // 채팅방 권한 양도
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
            if (!chatService.isRoomAdmin(roomId, currentMember.getMemberId())) {
                response.put("success", false);
                response.put("error", "방장 권한이 없습니다.");
                return response;
            }
            
            // 권한 양도
            chatService.transferRoomAdmin(roomId, currentMember.getMemberId(), newAdminId);
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
    public List<Member> getParticipants(@PathVariable("roomId") String roomId) {
        return chatService.getRoomParticipants(roomId);
    }

    // 채팅방 나가기
    @PostMapping("/api/chat/rooms/{roomId}/leave")
    @ResponseBody
    public Map<String, Object> leaveRoom(@PathVariable("roomId") String roomId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Member member = (Member) session.getAttribute("member");
            if (member == null) {
                throw new RuntimeException("로그인이 필요합니다.");
            }

            chatService.leaveRoom(roomId, member);
            
            // 퇴장 메시지 전송
            ChatMessage leaveMessage = new ChatMessage();
            leaveMessage.setType("LEAVE");
            leaveMessage.setSender(member.getMemberId());
            leaveMessage.setNickname(member.getNickname());
            leaveMessage.setRoomId(roomId);
            messagingTemplate.convertAndSend("/topic/chat/" + roomId, leaveMessage);
            
            response.put("success", true);
            response.put("message", "성공적으로 채팅방을 나갔습니다.");
        } catch (Exception e) {
            log.error("Error while leaving chat room: ", e);
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }
} 