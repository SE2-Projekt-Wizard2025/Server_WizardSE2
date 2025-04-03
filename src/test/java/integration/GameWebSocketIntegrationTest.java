package integration;

import com.aau.wizard.WizardApplication;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.response.GameResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = WizardApplication.class
)
class GameWebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private final BlockingQueue<GameResponse> responses = new ArrayBlockingQueue<>(1);

    @BeforeEach
    void setup() throws Exception {
        this.stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient())))
        );
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        String url = "ws://localhost:" + port + "/ws";
        this.stompSession = stompClient.connect(url, new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);

        stompSession.subscribe("/topic/game", new GameResponseFrameHandler());
    }

    @Test
    void shouldSendAndReceiveGameMessage() throws Exception {
        GameRequest request = new GameRequest("test-game-id");
        System.out.println("Sending request: " + request);

        stompSession.send("/app/game/play", request);

        GameResponse response = responses.poll(5, TimeUnit.SECONDS);
        System.out.println("Received response: " + response); // Add this

        assertNotNull(response, "No response received within timeout");
        assertEquals("test-game-id", response.getGameId());
    }

    private class GameResponseFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders headers) {
            return GameResponse.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {

            System.out.println("Received headers: " + headers);

            if (payload instanceof GameResponse) {
                responses.offer((GameResponse) payload);
            } else {
                System.err.println("Received unexpected payload: " + payload);
            }
        }
    }
}