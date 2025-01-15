package com.kylemall.shop.configuration;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NullPointerException.class)
    public String handleNullPointerException(NullPointerException e, HttpSession session, RedirectAttributes redirectAttributes) {
        log.error("NullPointerException 발생: {}", e.getMessage());
        
        // 세션 무효화
        session.invalidate();
        
        // 에러 메시지 추가
        redirectAttributes.addFlashAttribute("errorMessage", "세션이 만료되었습니다. 다시 로그인해주세요.");
        
        // 로그인 페이지로 리다이렉트
        return "redirect:/loginForm";
    }
} 