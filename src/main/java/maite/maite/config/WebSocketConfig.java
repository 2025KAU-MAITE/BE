package maite.maite.config;

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
        //일반 websocket 연결 허용
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*");

        // SockJS를 사용한 websocket 연결 허용
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue"); // 구독 주소 (topic: 그룹, queue: 개인)
        registry.setApplicationDestinationPrefixes("/app"); // 메시지 보낼 주소
        registry.setUserDestinationPrefix("/user"); // 개인 메시지를 위한 prefix
    }
}