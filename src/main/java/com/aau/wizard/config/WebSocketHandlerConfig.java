package com.aau.wizard.config;

import com.aau.wizard.controller.WebSocketHandlerImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketHandlerConfig implements WebSocketConfigurer {
    /**
     * Registers WebSocket handlers for incoming connections.
     * <p>
     * This handler is available at the endpoint <code>/ws</code>
     * and allows connections from all origins (CORS = "*").
     * </p>
     *
     * @param registry the {@link WebSocketHandlerRegistry} used to register WebSocket handlers
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandlerImpl(), "/ws")
                .setAllowedOrigins("*");
    }
}
