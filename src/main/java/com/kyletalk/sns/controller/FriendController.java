package com.kyletalk.sns.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import jakarta.servlet.http.HttpSession;
import com.kyletalk.sns.domain.Member;
import com.kyletalk.sns.service.FriendService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/friends")
@Slf4j
public class FriendController {
    
    @Autowired
    private FriendService friendService;
    
    @GetMapping("")
    public String friendPage(Model model, HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            return "redirect:/loginForm";
        }
        
        // 친구 목록 조회
        model.addAttribute("friends", friendService.getFriendList(member.getMemberId()));
        // 받은 친구 요청 조회
        model.addAttribute("friendRequests", friendService.getFriendRequests(member.getMemberId()));
        // 온라인 친구 조회
        model.addAttribute("onlineFriends", friendService.getOnlineFriends(member.getMemberId()));
        
        return "views/friends";
    }
    
    @PostMapping("/request")
    @ResponseBody
    public Map<String, Object> sendFriendRequest(@RequestParam("targetId") String targetId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Member member = (Member) session.getAttribute("member");
            friendService.sendFriendRequest(member.getMemberId(), targetId);
            response.put("success", true);
            response.put("message", "친구 요청을 보냈습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    @PostMapping("/accept")
    @ResponseBody
    public Map<String, Object> acceptFriendRequest(@RequestParam("requestId") String requestId) {
        Map<String, Object> response = new HashMap<>();
        try {
            friendService.acceptFriendRequest(requestId);
            response.put("success", true);
            response.put("message", "친구 요청을 수락했습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    @PostMapping("/decline")
    @ResponseBody
    public Map<String, Object> declineFriendRequest(@RequestParam("requestId") String requestId) {
        Map<String, Object> response = new HashMap<>();
        try {
            friendService.declineFriendRequest(requestId);
            response.put("success", true);
            response.put("message", "친구 요청을 거절했습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    @PostMapping("/remove")
    @ResponseBody
    public Map<String, Object> removeFriend(@RequestParam("friendId") String friendId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Member member = (Member) session.getAttribute("member");
            friendService.removeFriend(member.getMemberId(), friendId);
            response.put("success", true);
            response.put("message", "친구가 삭제되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    @GetMapping("/search")
    @ResponseBody
    public List<Member> searchUsers(@RequestParam("keyword") String keyword, HttpSession session) {
        log.info("Searching users with keyword: {}", keyword);
        
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            log.warn("User not logged in");
            return new ArrayList<>();
        }
        
        List<Member> results = friendService.searchAllUsers(keyword, member.getMemberId());
        log.info("Found {} users", results.size());
        
        return results;
    }
} 