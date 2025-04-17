package controller.unit;

import com.aau.wizard.controller.GameWebSocketController;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.service.interfaces.GameService;
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

    // TODO: Add further test attributes later on
    private static final String TEST_GAME_ID = "12345";
    private static final String TEST_PAYLOAD = "Game started successfully";

    @Test
    public void testStartGame() {
        GameRequest request = createDefaultGameRequest();
        GameResponse expectedResponse = createDefaultExpectedGameResponse();

        when(gameService.startGame(any(GameRequest.class))).thenReturn(expectedResponse);

        GameResponse response = gameWebSocketController.startGame(request);

        assertNotNull(response);
        assertEquals(TEST_GAME_ID, response.getGameId());
        assertEquals(TEST_PAYLOAD, response.getPayload());

        verify(gameService, times(1)).startGame(any(GameRequest.class));
    }

    @Test
    public void testStartGameWithNullResponse() {
        GameRequest request = createDefaultGameRequest();

        when(gameService.startGame(any(GameRequest.class))).thenReturn(null);

        GameResponse response = gameWebSocketController.startGame(request);

        assertNull(response);

        verify(gameService, times(1)).startGame(any(GameRequest.class));
    }

    private GameRequest createDefaultGameRequest() {
        // TODO: Add further attributes later on
        return new GameRequest(TEST_GAME_ID);
    }

    private GameResponse createDefaultExpectedGameResponse() {
        // TODO: Add further attributes later on
        return new GameResponse(TEST_GAME_ID, TEST_PAYLOAD);
    }
}
