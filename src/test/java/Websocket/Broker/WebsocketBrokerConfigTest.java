package Websocket.Broker;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import static org.mockito.Mockito.*;

public class WebsocketBrokerConfigTest {

    @Test
    void testConfigureMessageBroker() {
        WebsocketBrokerConfig config = new WebsocketBrokerConfig();
        MessageBrokerRegistry registry = mock(MessageBrokerRegistry.class);

        config.configureMessageBroker(registry);

        verify(registry).enableSimpleBroker("/topic");
        verify(registry).setApplicationDestinationPrefixes("/app");
    }

    @Test
    void testRegisterStompEndpoints() {
        WebsocketBrokerConfig config = new WebsocketBrokerConfig();
        StompEndpointRegistry registry = mock(StompEndpointRegistry.class);
        StompWebSocketEndpointRegistration registration = mock(StompWebSocketEndpointRegistration.class);

        when(registry.addEndpoint("/ws-broker")).thenReturn(registration);
        when(registration.setAllowedOrigins("*")).thenReturn(registration);

        config.registerStompEndpoints(registry);

        verify(registry).addEndpoint("/ws-broker");
        verify(registration).setAllowedOrigins("*");
    }
}
