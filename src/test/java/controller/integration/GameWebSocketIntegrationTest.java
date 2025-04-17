package controller.integration;

import com.aau.wizard.WizardApplication;
import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.model.enums.GameStatus;
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
import java.util.List;
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

    private static final String TEST_GAME_ID = "12345";
    private static final String TEST_PLAYER_ID = "Player1";
    private static final String TEST_PLAYER_NAME = "TestPlayer";

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

        GameRequest request = createDefaultGameRequest();
        session.send(JOIN_ENDPOINT, request);

        GameResponse response = blockingQueue.poll(5, TimeUnit.SECONDS);

        assertThat(response).isNotNull();
        assertThat(response.getGameId()).isEqualTo(TEST_GAME_ID);
        assertThat(response.getPlayers()).hasSize(1);
        assertThat(response.getPlayers().get(0).getPlayerId()).isEqualTo(TEST_PLAYER_ID);
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

    private PlayerDto createDefaultPlayerDto() {
        return new PlayerDto(TEST_PLAYER_ID, TEST_PLAYER_NAME, 0, false);
    }

    private GameResponse createDefaultGameResponse(PlayerDto testPlayer) {
        return new GameResponse(
                TEST_GAME_ID,
                GameStatus.LOBBY,
                TEST_PLAYER_ID,
                List.of(testPlayer),
                List.of(), // handCards empty (dummy)
                null       // lastPlayedCard
        );
    }

    private GameRequest createDefaultGameRequest() {
        GameRequest request = new GameRequest();
        request.setGameId(TEST_GAME_ID);
        request.setPlayerId(TEST_PLAYER_ID);
        request.setPlayerName(TEST_PLAYER_NAME);
        return request;
    }
}
