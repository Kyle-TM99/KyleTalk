package com.kyletalk.sns.controller;

import com.kyletalk.sns.domain.ChatMessage;
import com.kyletalk.sns.domain.ChatRoom;
import com.kyletalk.sns.domain.Member;
import com.kyletalk.sns.service.ChatService;
import com.kyletalk.sns.domain.NotificationMessage;
import com.kyletalk.sns.mapper.NotificationMapper;

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

    @Autowired
    private NotificationMapper notificationMapper;

    // 채팅방 삭제
    @PostMapping("/chat/delete/{roomId}")
    @ResponseBody
    public Map<String, Object> deleteChatRoom(@PathVariable("roomId") String roomId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Member currentMember = (Member) session.getAttribute("member");
            
            // 방장 권한 확인
            if (!chatService.isRoomAdmin(roomId, currentMember.getMemberId())) {
                throw new RuntimeException("방장만 채팅방을 삭제할 수 있습니다.");
            }
            
            // 모든 참여자에게 방 삭제 알림 전송
            ChatMessage systemMessage = new ChatMessage();
            systemMessage.setType("ROOM_DELETED");
            systemMessage.setRoomId(roomId);
            systemMessage.setMessage("채팅방이 방장에 의해 삭제되었습니다.");
            messagingTemplate.convertAndSend("/topic/chat/" + roomId, systemMessage);
            
            // 잠시 대기하여 메시지가 전송될 시간을 줌
            Thread.sleep(500);
            
            // 채팅방 삭제
            chatService.deleteAllMessages(roomId);
            chatService.deleteAllParticipants(roomId);
            chatService.deleteAllNotifications(roomId);
            chatService.deleteChatRoom(roomId);
            
            response.put("success", true);
            response.put("message", "채팅방이 삭제되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return response;
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
            if (room == null) {
                throw new RuntimeException("존재하지 않는 채팅방입니다.");
            }
            
            // 참여자 확인
            Member member = (Member) session.getAttribute("member");
            if (!chatService.isParticipant(roomId, member.getMemberId())) {
                chatService.addParticipant(roomId, member.getMemberId());
            }
            
            model.addAttribute("room", room);
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
        
        // 채팅 메시지 저장
        chatService.insertMessage(message);
        
        // 채팅방 참여자들에게 알림 생성
        List<String> participants = notificationMapper.getRoomParticipants(roomId);
        
        for (String participantId : participants) {
            // 메시지 발신자는 제외
            if (!participantId.equals(message.getSender())) {
                NotificationMessage notification = new NotificationMessage();
                notification.setMemberId(participantId);
                notification.setRoomId(roomId);
                notification.setMessage(message.getMessage());
                notification.setSender(message.getSender());
                notification.setSenderNickname(message.getNickname());
                notification.setType("CHAT");
                
                try {
                    // DB에 알림 저장
                    notificationMapper.insertNotification(notification);
                    log.info("Notification saved for user: {}", participantId);
                    
                    // 실시간 알림 전송
                    messagingTemplate.convertAndSend(
                        "/topic/notifications/" + participantId, 
                        notification
                    );
                    log.info("Real-time notification sent to user: {}", participantId);
                } catch (Exception e) {
                    log.error("Error sending notification: {}", e.getMessage());
                }
            }
        }
        
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
            
            // 여기서 기본 프로필 이미지가 설정될 수 있습니다
            if (message.getProfileImage() == null) {
                message.setProfileImage("/images/defaultProfile.png"); // 이 부분을 확인
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
            chatMessage.setRoomId(roomId);
            
            Member member = (Member) headerAccessor.getSessionAttributes().get("member");
            if (member != null) {
                chatMessage.setNickname(member.getNickname());
                chatMessage.setProfileImage(member.getMemberImage() != null ? 
                    member.getMemberImage() : "/images/defaultProfile.png");  // 이 부분도 확인
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