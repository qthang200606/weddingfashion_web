package com.aipo.weddingshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Cổng kết nối WebSocket từ Client/Admin lên Server
        registry.addEndpoint("/ws-chat").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Định tuyến tin nhắn đi (Broker)
        registry.enableSimpleBroker("/topic");
        // Tiền tố cho các API xử lý tin nhắn nhận vào
        registry.setApplicationDestinationPrefixes("/app");
    }
}