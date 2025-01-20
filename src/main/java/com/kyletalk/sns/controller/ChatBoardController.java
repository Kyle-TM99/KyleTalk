package com.kyletalk.sns.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kyletalk.sns.service.ChatBoardService;
import com.kyletalk.sns.domain.ChatBoardEvent;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/chat/board")
public class ChatBoardController {
    
    @Autowired
    private ChatBoardService chatBoardService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBoard(@RequestBody ChatBoardEvent board) {
        Map<String, Object> response = new HashMap<>();
        try {
            chatBoardService.createBoard(board);
            response.put("success", true);
            response.put("message", "게시글이 작성되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{roomId}")
    public ResponseEntity<List<ChatBoardEvent>> getBoardList(@PathVariable String roomId) {
        return ResponseEntity.ok(chatBoardService.getBoardList(roomId));
    }
    
    @GetMapping("/detail/{boardId}")
    public ResponseEntity<ChatBoardEvent> getBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(chatBoardService.getBoard(boardId));
    }
    
    @PutMapping("/{boardId}")
    public ResponseEntity<Map<String, Object>> updateBoard(
            @PathVariable Long boardId,
            @RequestBody ChatBoardEvent board) {
        Map<String, Object> response = new HashMap<>();
        try {
            board.setBoardId(boardId);
            chatBoardService.updateBoard(board);
            response.put("success", true);
            response.put("message", "게시글이 수정되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Map<String, Object>> deleteBoard(@PathVariable Long boardId) {
        Map<String, Object> response = new HashMap<>();
        try {
            chatBoardService.deleteBoard(boardId);
            response.put("success", true);
            response.put("message", "게시글이 삭제되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
} 