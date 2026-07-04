package com.testBackendDatabase.demo.WebSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.lang.NonNull;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        // Kích hoạt một broker nội bộ để gửi tin nhắn từ Server -> Client
        config.enableSimpleBroker("/topic", "/queue");
        // Tiền tố cho các request từ Client gửi lên Server (nếu có)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // Đường dẫn Endpoint để Client kết nối Socket (đăng ký SockJS để tương thích trình duyệt cũ)
        registry.addEndpoint("/ws-payment")
                .setAllowedOriginPatterns("*") // Cấu hình CORS tùy theo dự án
                .withSockJS();
    }
}
