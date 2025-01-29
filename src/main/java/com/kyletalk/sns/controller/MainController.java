package com.kyletalk.sns.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.kyletalk.sns.service.ChatService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MainController {
	
	@Autowired
	private ChatService chatService;
	
	@GetMapping({"/", "/mainPage"})
	public String mainPage(Model model) {
		// 참여자 수가 많은 상위 6개의 채팅방을 가져옴
		model.addAttribute("hotRooms", chatService.getHotChatRooms(6));
		return "views/mainPage";
	}
	
	@GetMapping("/loginForm")
	public String loginForm(Model model) {
		return "member/loginForm";
	}
}
