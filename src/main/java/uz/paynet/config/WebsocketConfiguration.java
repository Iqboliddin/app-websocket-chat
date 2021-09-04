package uz.paynet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import uz.paynet.service.WebsocketHandlerService;

@Configuration
@EnableWebSocket
public class WebsocketConfiguration implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(getChatWebsocketHandler(), "/api/chat").setAllowedOrigins("*");
    }

    @Bean
    WebSocketHandler getChatWebsocketHandler() {
        return new WebsocketHandlerService();
    }
}
