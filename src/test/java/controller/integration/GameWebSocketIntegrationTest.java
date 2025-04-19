package controller.integration;

import com.aau.wizard.WizardApplication;
import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.service.interfaces.GameService;
import static com.aau.wizard.testutil.TestConstants.*;
import static com.aau.wizard.testutil.TestDataFactory.*;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for WebSocket communication related to game actions.
 * Uses mocked GameService to isolate WebSocket behavior.
 */
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

    private static final String GAME_TOPIC = "/topic/game";
    private static final String JOIN_ENDPOINT = "/app/game/join";

    /**
     * Prepares the mocked GameService and sets up a stubbed GameResponse
     * that will be returned when joinGame(...) is called.
     */
    @BeforeEach
    void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        PlayerDto testPlayer = createDefaultPlayerDto();
        GameResponse mockedResponse = createDefaultGameResponse(testPlayer);

        Mockito.when(gameService.joinGame(Mockito.any(GameRequest.class)))
                .thenReturn(mockedResponse);
    }

    /**
     * Integration test for the /app/game/join WebSocket endpoint.
     * Verifies that a GameRequest sent over WebSocket results in a proper GameResponse
     * being published to /topic/game.
     */
    @Test
    @Timeout(10)
    void testJoinGameWebSocketEndpoint() throws Exception {
        StompSession session = connectToWebSocket();
        subscribeToGameTopic(session);

        sendJoinRequest(session);

        GameResponse response = blockingQueue.poll(5, TimeUnit.SECONDS);
        assertJoinResponse(response);
    }

    /**
     * Connects to the WebSocket endpoint using STOMP.
     *
     * @return an active StompSession
     */
    private StompSession connectToWebSocket() throws Exception {
        String url = "ws://localhost:" + port + "/ws";
        return stompClient
                .connect(url, headers, new StompSessionHandlerAdapter() {})
                .get(2, TimeUnit.SECONDS);
    }

    /**
     * Subscribes to the /topic/game topic and pushes all incoming GameResponse
     * frames into a blocking queue for assertion.
     */
    private void subscribeToGameTopic(StompSession session) {
        session.subscribe(GAME_TOPIC, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return GameResponse.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.offer((GameResponse) payload);
            }
        });
    }

    /**
     * Sends a join game request over the given WebSocket STOMP session.
     * <p>
     * Uses the predefined {@code JOIN_ENDPOINT} and a default test {@link GameRequest}.
     *
     * @param session the active {@link StompSession} used to send the message
     */
    private void sendJoinRequest(StompSession session) {
        session.send(JOIN_ENDPOINT, createDefaultGameRequest());
    }

    /**
     * Asserts that the received {@link GameResponse} contains the expected game ID
     * and exactly one player with the correct player ID.
     *
     * @param response the {@link GameResponse} to validate
     */
    private void assertJoinResponse(GameResponse response) {
        assertThat(response).isNotNull();
        assertThat(response.getGameId()).isEqualTo(TEST_GAME_ID);
        assertThat(response.getPlayers()).hasSize(2);
        assertThat(response.getPlayers().get(0).getPlayerId()).isEqualTo(TEST_PLAYER_ID);
    }
}
