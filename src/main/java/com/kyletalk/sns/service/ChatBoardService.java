package com.kyletalk.sns.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kyletalk.sns.mapper.ChatBoardMapper;
import com.kyletalk.sns.domain.ChatBoardEvent;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatBoardService {
    
    @Autowired
    private ChatBoardMapper chatBoardMapper;
    
    public void createBoard(ChatBoardEvent board) {
        chatBoardMapper.insertBoard(board);
    }
    
    public List<ChatBoardEvent> getBoardList(String roomId) {
        return chatBoardMapper.getBoardList(roomId);
    }
    
    public ChatBoardEvent getBoard(Long boardId) {
        return chatBoardMapper.getBoard(boardId);
    }
    
    public void updateBoard(ChatBoardEvent board) {
        chatBoardMapper.updateBoard(board);
    }
    
    public void deleteBoard(Long boardId) {
        chatBoardMapper.deleteBoard(boardId);
    }
} 