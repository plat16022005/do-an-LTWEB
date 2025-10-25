package aloute.com.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")           // endpoint để kết nối
                .setAllowedOriginPatterns("*") // cho phép tất cả origin trong dev
                .withSockJS();                // fallback nếu trình duyệt không hỗ trợ WS
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // client gửi message đến /app/...
        registry.setApplicationDestinationPrefixes("/app");
        // server gửi ngược message cho client qua /topic/... hoặc /queue/...
        registry.enableSimpleBroker("/topic");
    }
}
