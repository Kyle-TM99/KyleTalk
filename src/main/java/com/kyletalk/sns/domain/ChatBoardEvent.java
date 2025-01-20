package com.kyletalk.sns.domain;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChatBoardEvent {
    private Long boardId;
    private String roomId;
    private String memberId;
    private String boardTitle;
    private String boardContent;
    private LocalDateTime joinedAt;
    
    // 추가 필드: 작성자 정보
    private String writerNickname;
    private String writerImage;
    
    // 게시글 메타 데이터
    private int viewCount;
    private int likeCount;
    private int commentCount;
    
    // 현재 로그인한 사용자 관련
    private boolean isWriter;        // 현재 사용자가 작성자인지
    private boolean isLiked;         // 현재 사용자가 좋아요 했는지
} 