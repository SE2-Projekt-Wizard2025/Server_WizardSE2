package controller.unit;

import com.aau.wizard.controller.GameWebSocketController;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.request.PredictionRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.model.enums.GameStatus;
import com.aau.wizard.service.interfaces.GameService;
import static com.aau.wizard.testutil.TestConstants.*;
import static com.aau.wizard.testutil.TestDataFactory.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameWebSocketControllerTest {

    @Mock
    private GameService gameService;

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
        GameResponse response = gameWebSocketController.joinGame(request);

        assertBasicJoinResponse(response);
        verifyJoinCalledOnce();
    }

    /**
     * Tests that joinGame() returns null when the GameService returns null.
     */
    @Test
    void testJoinGameWithNullResponse() {
        GameRequest request = createDefaultGameRequest();

        when(gameService.joinGame(any(GameRequest.class))).thenReturn(null);
        GameResponse response = gameWebSocketController.joinGame(request);

        assertNull(response);
        verifyJoinCalledOnce();
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

}
