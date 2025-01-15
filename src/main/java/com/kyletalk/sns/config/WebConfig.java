package com.kylemall.shop.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kylemall.shop.interceptor.LoginCheckInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {

		registry.addViewController("/productPayment").setViewName("views/productPayment");
		registry.addViewController("/mainList").setViewName("views/mainList");
		registry.addViewController("/loginForm").setViewName("member/loginForm");
		registry.addViewController("/joinForm").setViewName("member/memberJoinForm");
		registry.addViewController("/productList").setViewName("views/productList");
		registry.addViewController("/joinChoice").setViewName("member/joinChoice");
		registry.addViewController("/overlapIdCheck").setViewName("member/overlapIdCheck");
		//registry.addViewController("/oauth/kakao/additionalInfo").setViewName("member/additionalInfo");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**")
				.addResourceLocations("classpath:/static/");
	}

	  @Override 
	  public void addInterceptors(InterceptorRegistry registry) {
		  registry.addInterceptor(new LoginCheckInterceptor())
	  		   	  .addPathPatterns("/**") // 인터셉터를 적용할 경로
	              .excludePathPatterns( // 로그인 없이 접근 가능한 경로
	                      "/",              // 메인 페이지
	                      "/mainList",		// 메인 페이지
	                      "/productList",   // 상품 리스트
	                      "/member/**",
	                      "/login",
	                      "/loginForm",     // 로그인 페이지
	                      "/memberJoinForm",    // 회원가입   
	                      "/joinForm", // 회원가입 페이지
	                      "/overlapIdCheck", // 아이디 중복 확인 페이지
	                      "/joinResult",     
	                      "/oauth/kakao/**", // 카카오 로그인 관련 모든 경로 추가
	                      "/error",         // 에러 페이지 추가
	                      "/css/**",        // 정적 자원 (CSS)
	                      "/js/**",         // 정적 자원 (JS)
	                      "/images/**",      // 정적 자원 (이미지)
	                      "/kylemallproducts/**",      // 정적 자원 (이미지)
	                      "/assets/**",
	                      "/bootstrap/**",
												"/google/**",  // Google OAuth2 관련 경로 제외
               				  "/loginForm/**"
	              );
	  }


}
