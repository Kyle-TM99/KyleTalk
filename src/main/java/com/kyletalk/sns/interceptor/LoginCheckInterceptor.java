package com.kylemall.shop.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// 접속자가 로그인 상태인지 체크하는 인터셉터
@Component
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
	

	@Override
	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();
		
		try {
			// 세션에서 member와 isLogin 확인
			Boolean isLogin = (Boolean) session.getAttribute("isLogin");
			Object member = session.getAttribute("member");
			
			if (isLogin == null || !isLogin || member == null) {
				log.warn("인증되지 않� 사용자 접근");
				session.invalidate(); // 세션 무효화
				response.sendRedirect("/loginForm");
				return false;
			}
			
			return true;
			
		} catch (Exception e) {
			log.error("Interceptor에서 예외 �생: {}", e.getMessage());
			session.invalidate(); // 세션 무효화
			response.sendRedirect("/loginForm");
			return false;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		log.info("##########LoginCheckInterceptor - postHandle()##########");		

		response.setHeader("Cache-Control", "no-cache");
	}
	

	@Override
	public void afterCompletion(HttpServletRequest request, 
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		log.info("##########LoginCheckInterceptor - afterCompletion()##########");
	}	
}
