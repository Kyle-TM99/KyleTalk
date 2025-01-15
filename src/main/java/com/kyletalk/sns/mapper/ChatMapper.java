package com.kyletalk.sns.mapper;

import com.kyletalk.sns.domain.ChatMessage;
import com.kyletalk.sns.domain.ChatRoom;
import com.kyletalk.sns.domain.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChatMapper {
    void insertMessage(ChatMessage message);
    List<ChatMessage> getRecentMessages(@Param("roomId") String roomId, @Param("limit") int limit);
    void createChatRoom(ChatRoom room);
    List<ChatRoom> getAllChatRooms();
    ChatRoom getChatRoomById(@Param("roomId") String roomId);
    void updateParticipantCount(@Param("roomId") String roomId, @Param("count") int count);
    String getRoomPassword(@Param("roomId") String roomId);
    void addParticipant(@Param("roomId") String roomId, @Param("memberId") String memberId);
    void removeParticipant(@Param("roomId") String roomId, @Param("memberId") String memberId);
    boolean isParticipant(@Param("roomId") String roomId, @Param("memberId") String memberId);
    int getParticipantCount(@Param("roomId") String roomId);
    void deleteChatRoom(@Param("roomId") String roomId);
    boolean isRoomAdmin(@Param("roomId") String roomId, @Param("memberId") String memberId);
    void transferRoomAdmin(@Param("roomId") String roomId, 
                          @Param("currentAdmin") String currentAdmin,
                          @Param("newAdmin") String newAdmin);
    List<Member> getRoomParticipants(@Param("roomId") String roomId);
} 