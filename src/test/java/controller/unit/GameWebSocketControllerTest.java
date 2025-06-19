package controller.unit;

import com.aau.wizard.controller.GameWebSocketController;
import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.request.PredictionRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.model.enums.GameStatus;
import com.aau.wizard.service.interfaces.GameService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static testutil.TestDataFactory.*;
import static testutil.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class GameWebSocketControllerTest {

    @Mock
    private GameService gameService;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private GameWebSocketController gameWebSocketController;

    /**
     * Tests that joinGame() returns a valid GameResponse when the GameService returns one.
     */
    @Test
    void testJoinGame() {
        GameRequest request = createDefaultGameRequest();
        GameResponse expectedResponse = createDefaultGameResponse(createDefaultPlayerDto());

        when(gameService.joinGame(any(GameRequest.class))).thenReturn(expectedResponse);

        gameWebSocketController.joinGame(request);

        
        verify(gameService, times(1)).joinGame(any(GameRequest.class));
        verify(messagingTemplate, times(1)).convertAndSend("/topic/game", expectedResponse);

    }



    @Test
    void testStartGameCleansQuotedGameId() {
        String quotedGameId = "\"test-game-id\"";
        gameWebSocketController.startGame(quotedGameId);
        verify(gameService).startGame("test-game-id");
    }

    /**
     * Asserts that the given {@link GameResponse} contains the expected game ID,
     * status, and player ID for a successful join operation.
     *
     * @param response the {@link GameResponse} returned by the controller
     */
    private void assertBasicJoinResponse(GameResponse response) {
        assertNotNull(response);
        assertEquals(TEST_GAME_ID, response.getGameId());
        assertEquals(GameStatus.LOBBY, response.getStatus());
        assertEquals(TEST_PLAYER_ID, response.getPlayers().get(0).getPlayerId());
    }

    /**
     * Verifies that the {@code joinGame} method of the {@link GameService}
     * was called exactly once during the test.
     */
    private void verifyJoinCalledOnce() {
        verify(gameService, times(1)).joinGame(any(GameRequest.class));
    }

    @Test
    void testHandlePredictionCallsGameService() {
        PredictionRequest request = new PredictionRequest(TEST_GAME_ID, TEST_PLAYER_ID, 2);
        GameResponse expectedResponse = createDefaultGameResponse(createDefaultPlayerDto());

        when(gameService.makePrediction(any(PredictionRequest.class))).thenReturn(expectedResponse);

        GameResponse response = gameWebSocketController.handlePrediction(request);

        assertNotNull(response);
        assertEquals(TEST_GAME_ID, response.getGameId());
        assertEquals(TEST_PLAYER_ID, response.getPlayers().get(0).getPlayerId());

        verify(gameService, times(1)).makePrediction(any(PredictionRequest.class));
    }


    @Test
    void testPlayCard() {
        GameRequest request = createDefaultGameRequest();
        GameResponse expectedResponse = createDefaultGameResponse(createDefaultPlayerDto());

        when(gameService.playCard(any(GameRequest.class))).thenReturn(expectedResponse);

        GameResponse response = gameWebSocketController.playCard(request);

        assertNotNull(response);
        assertEquals(TEST_GAME_ID, response.getGameId());
        verify(gameService, times(1)).playCard(request);
    }

    @Test
    void testSendScoreboard() {
        String gameId = TEST_GAME_ID;
        List<PlayerDto> expectedScoreboard = List.of(createDefaultPlayerDto());

        when(gameService.getScoreboard(gameId)).thenReturn(expectedScoreboard);

        List<PlayerDto> result = gameWebSocketController.sendScoreboard(gameId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TEST_PLAYER_ID, result.get(0).getPlayerId());
        verify(gameService, times(1)).getScoreboard(gameId);
    }

    @Test
    void testStartGameWithUnquotedGameId() {
        String unquotedGameId = "test-game-id";
        gameWebSocketController.startGame(unquotedGameId);
        verify(gameService).startGame(unquotedGameId);
    }

}
