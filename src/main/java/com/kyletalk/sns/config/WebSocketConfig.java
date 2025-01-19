package com.kyletalk.sns.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    // 메시지 브로커 설정
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 브로커 설정
        config.enableSimpleBroker("/topic");
        // 애플리케이션 목적지 접두사 설정
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    // STOMP 엔드포인트 등록
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 엔드포인트 등록
        registry.addEndpoint("/ws")
                // 모든 출처 허용
                .setAllowedOriginPatterns("*")
                // SockJS 사용
                .withSockJS();
    }
} 