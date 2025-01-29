package com.kyletalk.sns.mapper;

import com.kyletalk.sns.domain.Friend;
import com.kyletalk.sns.domain.FriendRequest;
import com.kyletalk.sns.domain.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FriendMapper {
    // 친구 목록 조회
    List<Friend> getFriendList(@Param("memberId") String memberId);
    
    // 온라인 친구 조회
    List<Friend> getOnlineFriends(@Param("memberId") String memberId);
    
    // 친구 요청 목록 조회
    List<FriendRequest> getFriendRequests(@Param("memberId") String memberId);
    
    // 친구 요청 조회
    FriendRequest getFriendRequest(@Param("requestId") String requestId);
    
    // 친구 관계 확인
    boolean isFriend(@Param("memberId1") String memberId1, @Param("memberId2") String memberId2);
    
    // 친구 요청 존재 확인
    boolean hasRequestPending(@Param("fromMemberId") String fromMemberId, @Param("toMemberId") String toMemberId);
    
    // 친구 요청 생성
    void createFriendRequest(@Param("fromMemberId") String fromMemberId, @Param("toMemberId") String toMemberId);
    
    // 친구 관계 생성
    void createFriendship(@Param("memberId1") String memberId1, @Param("memberId2") String memberId2);
    
    // 친구 요청 삭제
    void deleteFriendRequest(@Param("requestId") String requestId);
    
    // 친구 관계 삭제
    void deleteFriendship(@Param("memberId1") String memberId1, @Param("memberId2") String memberId2);
    
    // 친구 검색
    List<Friend> searchFriends(@Param("memberId") String memberId, @Param("keyword") String keyword);
    
    // 전체 사용자 검색 (친구가 아닌 사용자 포함)
    List<Member> searchAllUsers(@Param("keyword") String keyword, @Param("currentUserId") String currentUserId);
} 