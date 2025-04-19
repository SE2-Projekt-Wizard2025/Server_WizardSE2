package Websocket.Handler;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public class WebsocketHandlerImpTest {

    @Test
    void testAfterConnectionEstablished() {
        WebSocketSession session = mock(WebSocketSession.class);
        WebsocketHandlerImp handler = new WebsocketHandlerImp();

        handler.afterConnectionEstablished(session);

        verify(session, times(1)).getId();
    }

    @Test
    void testHandleMessage() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);

        WebSocketMessage<String> message = mock(WebSocketMessage.class);
        when(message.getPayload()).thenReturn("Test Message");

        WebsocketHandlerImp handler = new WebsocketHandlerImp();

        handler.handleMessage(session, message);

        verify(session, times(1)).sendMessage(any(TextMessage.class));

        verify(session).sendMessage(argThat(textMessage -> textMessage.getPayload().equals("Echo vom Server: Test Message")));
    }

    @Test
    void testHandleTransportError() {
        WebSocketSession session = mock(WebSocketSession.class);
        Throwable exception = new Throwable("Test Error");

        WebsocketHandlerImp handler = new WebsocketHandlerImp();

        handler.handleTransportError(session, exception);
    }

    @Test
    void testAfterConnectionClosed() {
        WebSocketSession session = mock(WebSocketSession.class);
        CloseStatus status = CloseStatus.NORMAL;

        WebsocketHandlerImp handler = new WebsocketHandlerImp();

        handler.afterConnectionClosed(session, status);

        verify(session, times(1)).getId();
    }

    @Test
    void testSupportsPartialMessages() {
        WebsocketHandlerImp handler = new WebsocketHandlerImp();

        boolean supportsPartial = handler.supportsPartialMessages();

        assertFalse(supportsPartial);
    }
}
