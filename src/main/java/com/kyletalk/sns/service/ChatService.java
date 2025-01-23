package com.kyletalk.sns.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kyletalk.sns.mapper.ChatMapper;
import com.kyletalk.sns.domain.ChatMessage;
import com.kyletalk.sns.domain.ChatRoom;
import java.util.List;
import java.util.UUID;
import java.sql.Timestamp;
import com.kyletalk.sns.domain.Member;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatService {
    
    @Autowired
    private ChatMapper chatMapper;


    // 채팅방 삭제
    public void deleteChatRoom(String roomId) {
        // 1. 채팅방의 모든 메시지 삭제
        chatMapper.deleteAllMessages(roomId);
        
        // 2. 채팅방의 모든 참여자 정보 삭제
        chatMapper.deleteAllParticipants(roomId);
        
        // 3. 채팅방 삭제
        chatMapper.deleteChatRoom(roomId);
    }
    
    // 채팅방 목록 조회
    public List<ChatRoom> getAllChatRooms() {
        return chatMapper.getAllChatRooms();
    }

    // 채팅방 생성
    public String createRoom(ChatRoom room, Member member) {
        room.setRoomId(UUID.randomUUID().toString());
        room.setCreatedBy(member.getMemberId());
        room.setRoomAdmin(member.getMemberId());
        room.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        room.setCurrentUsers(1);
        
        chatMapper.createChatRoom(room);
        chatMapper.addParticipant(room.getRoomId(), member.getMemberId());
        
        return room.getRoomId();
    }

    // 채팅방 비밀번호 확인
    public void verifyAndJoinRoom(String roomId, String password, Member member) {
        String storedPassword = chatMapper.getRoomPassword(roomId);
        if (storedPassword == null || !storedPassword.equals(password)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        
        chatMapper.addParticipant(roomId, member.getMemberId());
        int currentCount = chatMapper.getParticipantCount(roomId);
        chatMapper.updateParticipantCount(roomId, currentCount);
    }

    // 채팅방 참여자 조회
    public List<Member> getRoomParticipants(String roomId) {
        return chatMapper.getRoomParticipants(roomId);
    }

    // 채팅방 나가기
    public void leaveRoom(String roomId, Member member) {
        // 참여자인지 확인
        boolean isParticipant = chatMapper.isParticipant(roomId, member.getMemberId());
        if (!isParticipant) {
            throw new RuntimeException("해당 채팅방의 참여자가 아닙니다.");
        }

        // 방장인지 확인
        boolean isAdmin = chatMapper.isRoomAdmin(roomId, member.getMemberId());
        if (isAdmin) {
            throw new RuntimeException("방장은 방을 나갈 수 없습니다. 먼저 방장을 위임해주세요.");
        }
        
        // 참여자 제거
        chatMapper.removeParticipant(roomId, member.getMemberId());

        // 참여자 수 업데이트
        int currentCount = chatMapper.getParticipantCount(roomId);
        
        // 참여자 수가 0이면 채팅방 삭제
        if (currentCount == 0) {
            chatMapper.deleteChatRoom(roomId);
        } else {
            // 참여자 수 업데이트
            chatMapper.updateParticipantCount(roomId, currentCount);
        }
    }

    // 채팅방 조회
    public ChatRoom findRoom(String roomId) {
        // 1. 방 존재 여부 확인
        ChatRoom room = chatMapper.getChatRoomById(roomId);
        if (room == null) {
            throw new RuntimeException("존재하지 않는 채팅방입니다.");
        }
        
        // 2. 최근 메시지 목록 조회
        List<ChatMessage> recentMessages = chatMapper.getRecentMessages(roomId, 50);
        room.setRecentMessages(recentMessages);
        
        // 3. 현재 참여자 수 조회
        int participantCount = chatMapper.getParticipantCount(roomId);
        room.setCurrentUsers(participantCount);
        
        return room;
    }

    // 채팅 메시지 삽입
    public void insertMessage(ChatMessage message) {
        chatMapper.insertMessage(message);
    }

    // 채팅방 권한 양도
    public void transferRoomAdmin(String roomId, String currentAdmin, String newAdmin) {
        if (!isRoomAdmin(roomId, currentAdmin)) {
            throw new RuntimeException("방장 권한이 없습니다.");
        }
        if (!isParticipant(roomId, newAdmin)) {
            throw new RuntimeException("해당 사용자가 채팅방에 참여하지 않았습니다.");
        }
        chatMapper.transferRoomAdmin(roomId, currentAdmin, newAdmin);
    }

    // 채팅방 권한 확인
    public boolean isRoomAdmin(String roomId, String memberId) {
        return chatMapper.isRoomAdmin(roomId, memberId);
    }

    // 채팅방 참여자 확인
    public boolean isParticipant(String roomId, String memberId) {
        return chatMapper.isParticipant(roomId, memberId);
    }

    // 채팅방 참여자 추가
    public void addParticipant(String roomId, String memberId) {
        if (!isParticipant(roomId, memberId)) {
            chatMapper.addParticipant(roomId, memberId);
            int currentCount = chatMapper.getParticipantCount(roomId);
            chatMapper.updateParticipantCount(roomId, currentCount);
        }
    }

    // 채팅방 참여자 제거
    public void removeParticipant(String roomId, String memberId) {
        if (isParticipant(roomId, memberId)) {
            chatMapper.removeParticipant(roomId, memberId);
            int currentCount = chatMapper.getParticipantCount(roomId);
            if (currentCount == 0) {
                chatMapper.deleteChatRoom(roomId);
            } else {
                chatMapper.updateParticipantCount(roomId, currentCount);
            }
        }
    }

    public void deleteAllMessages(String roomId) {
        chatMapper.deleteAllMessages(roomId);
    }

    public void deleteAllParticipants(String roomId) {
        chatMapper.deleteAllParticipants(roomId);
    }

    public void deleteAllNotifications(String roomId) {
        chatMapper.deleteAllNotifications(roomId);
    }
} 