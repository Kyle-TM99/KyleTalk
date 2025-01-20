package com.kyletalk.sns.mapper;

import com.kyletalk.sns.domain.ChatBoardEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChatBoardMapper {
    // 게시글 작성
    void insertBoard(ChatBoardEvent board);
    
    // 게시글 목록 조회
    List<ChatBoardEvent> getBoardList(@Param("roomId") String roomId);
    
    // 게시글 상세 조회
    ChatBoardEvent getBoard(@Param("boardId") Long boardId);
    
    // 게시글 수정
    void updateBoard(ChatBoardEvent board);
    
    // 게시글 삭제
    void deleteBoard(@Param("boardId") Long boardId);
    
    // 조회수 증가
    void increaseViewCount(@Param("boardId") Long boardId);
} 