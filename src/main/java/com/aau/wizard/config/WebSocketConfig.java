package com.aau.wizard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * Configures the message broker for routing messages between clients and the server.
     * <p>
     * Enables a simple in-memory message broker for broadcasting messages to destinations
     * prefixed with "/topic". Also sets "/app" as the prefix for messages sent from clients
     * that should be routed to message-handling methods annotated with {@code @MessageMapping}.
     *
     * @param config the configuration object for setting up the message broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers the STOMP (Simple Text Oriented Messaging Protocol) endpoint for WebSocket communication.
     * <p>
     * This configuration defines the endpoint that clients will use to connect to the WebSocket server.
     * It allows all origins (for testing purposes).
     *
     * @param registry the registry to which the WebSocket endpoint is added
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*"); // CORS for local tests
    }
}
