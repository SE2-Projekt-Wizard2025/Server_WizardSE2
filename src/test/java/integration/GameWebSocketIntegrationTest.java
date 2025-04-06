package integration;

import com.aau.wizard.WizardApplication;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.service.interfaces.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = WizardApplication.class)
@ActiveProfiles("test")
public class GameWebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    @MockBean
    private GameService gameService;

    private WebSocketStompClient stompClient;
    private final BlockingQueue<GameResponse> blockingQueue = new LinkedBlockingDeque<>();
    private static final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    // TODO: Add further test attributes later on
    private static final String TEST_GAME_ID = "12345";
    private static final String TEST_PAYLOAD = "Game started successfully";

    @BeforeEach
    void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        GameResponse mockedResponse = new GameResponse(TEST_GAME_ID, TEST_PAYLOAD);

        Mockito.when(gameService.startGame(Mockito.any(GameRequest.class)))
                .thenReturn(mockedResponse);
    }

    @Test
    @Timeout(10)
    void testGamePlayWebSocketEndpoint() throws Exception {
        String url = "ws://localhost:" + port + "/ws";
        StompSession session = stompClient
                .connect(url, headers, new StompSessionHandlerAdapter() {})
                .get(2, TimeUnit.SECONDS);

        session.subscribe("/topic/game", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return GameResponse.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.offer((GameResponse) payload);
            }
        });

        GameRequest request = new GameRequest(TEST_GAME_ID);
        session.send("/app/game/play", request);

        GameResponse response = blockingQueue.poll(5, TimeUnit.SECONDS);

        assertThat(response).isNotNull();
        assertThat(response.getGameId()).isEqualTo(TEST_GAME_ID);
        assertThat(response.getPayload()).isEqualTo(TEST_PAYLOAD);
    }
}
