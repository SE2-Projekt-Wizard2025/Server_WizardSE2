package Websocket.Handler;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;

public class WebsocketHandlerConfigTest {

    @Test
    void testRegisterWebSocketHandlers() {
        WebsocketHandlerConfig config = new WebsocketHandlerConfig();
        WebSocketHandlerRegistry registry = mock(WebSocketHandlerRegistry.class);

        WebSocketHandlerRegistration handlerRegistration = mock(WebSocketHandlerRegistration.class);

        when(registry.addHandler(any(), eq("/ws"))).thenReturn(handlerRegistration);

        config.registerWebSocketHandlers(registry);

        verify(registry).addHandler(any(), eq("/ws"));
        verify(handlerRegistration).setAllowedOrigins("*");
    }
}
