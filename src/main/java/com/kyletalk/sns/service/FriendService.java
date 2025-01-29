package com.kyletalk.sns.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kyletalk.sns.mapper.FriendMapper;
import com.kyletalk.sns.domain.Friend;
import com.kyletalk.sns.domain.FriendRequest;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import com.kyletalk.sns.domain.Member;

@Service
@Slf4j
public class FriendService {
    
    @Autowired
    private FriendMapper friendMapper;
    
    public List<Friend> getFriendList(String memberId) {
        return friendMapper.getFriendList(memberId);
    }
    
    public List<Friend> getOnlineFriends(String memberId) {
        return friendMapper.getOnlineFriends(memberId);
    }
    
    public List<FriendRequest> getFriendRequests(String memberId) {
        return friendMapper.getFriendRequests(memberId);
    }
    
    public void sendFriendRequest(String fromMemberId, String toMemberId) {
        // 이미 친구인지 확인
        if (friendMapper.isFriend(fromMemberId, toMemberId)) {
            throw new RuntimeException("이미 친구입니다.");
        }
        
        // 이미 요청을 보냈는지 확인
        if (friendMapper.hasRequestPending(fromMemberId, toMemberId)) {
            throw new RuntimeException("이미 친구 요청을 보냈습니다.");
        }
        
        friendMapper.createFriendRequest(fromMemberId, toMemberId);
    }
    
    public void acceptFriendRequest(String requestId) {
        FriendRequest request = friendMapper.getFriendRequest(requestId);
        if (request == null) {
            throw new RuntimeException("존재하지 않는 친구 요청입니다.");
        }
        
        // 친구 관계 생성
        friendMapper.createFriendship(request.getFromMemberId(), request.getToMemberId());
        // 요청 삭제
        friendMapper.deleteFriendRequest(requestId);
    }
    
    public void declineFriendRequest(String requestId) {
        friendMapper.deleteFriendRequest(requestId);
    }
    
    public void removeFriend(String memberId, String friendId) {
        friendMapper.deleteFriendship(memberId, friendId);
    }
    
    public List<Friend> searchFriends(String memberId, String keyword) {
        return friendMapper.searchFriends(memberId, keyword);
    }
    
    public List<Member> searchAllUsers(String keyword, String currentUserId) {
        return friendMapper.searchAllUsers(keyword, currentUserId);
    }
} 