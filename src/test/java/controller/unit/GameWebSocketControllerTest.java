package controller.unit;

import com.aau.wizard.controller.GameWebSocketController;
import com.aau.wizard.dto.CardDto;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.model.enums.GameStatus;
import com.aau.wizard.service.interfaces.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameWebSocketControllerTest {

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameWebSocketController gameWebSocketController;

    private static final String TEST_GAME_ID = "12345";
    private static final String TEST_PLAYER_ID = "player1";
    private static final String TEST_PLAYER_NAME = "TestPlayer";

    /**
     * Tests that joinGame() returns a valid GameResponse when the GameService returns one.
     */
    @Test
    void testJoinGame() {
        GameRequest request = createDefaultGameRequest();
        GameResponse expectedResponse = createDefaultExpectedGameResponse();

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

    private GameRequest createDefaultGameRequest() {
        GameRequest request = new GameRequest();
        request.setGameId(TEST_GAME_ID);
        request.setPlayerId(TEST_PLAYER_ID);
        request.setPlayerName(TEST_PLAYER_NAME);
        return request;
    }

    private GameResponse createDefaultExpectedGameResponse() {
        PlayerDto playerDto = createDefaultPlayerDto();
        List<CardDto> cardDtos = createDefaultListOfCardDto();
        return new GameResponse(
                TEST_GAME_ID,
                GameStatus.LOBBY,
                TEST_PLAYER_ID,
                List.of(playerDto),
                cardDtos,
                null       // lastPlayedCard
        );
    }

    private PlayerDto createDefaultPlayerDto() {
        return new PlayerDto(TEST_PLAYER_ID, TEST_PLAYER_NAME, 0, false);
    }

    private List<CardDto> createDefaultListOfCardDto() {
        return List.of(
                new CardDto("RED", "ONE", "NORMAL"),
                new CardDto("BLUE", "TWO", "FOOL")
        );
    }
}
