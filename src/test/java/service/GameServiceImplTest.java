package service;

import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.model.Game;
import com.aau.wizard.model.enums.GameStatus;
import com.aau.wizard.service.impl.GameServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceImplTest {

    @InjectMocks
    private GameServiceImpl gameService;

    private static final String TEST_GAME_ID = "12345";
    private static final String TEST_PLAYER_ID = "player1";
    private static final String TEST_PLAYER_NAME = "TestPlayer";

    /**
     * Verifies that a new game is created and a player is added when a player joins
     * a non-existent game. Also checks that the game state is returned correctly.
     */
    @Test
    void testJoinGameCreatesGameAndAddsPlayer() {
        GameRequest request = createDefaultGameRequest();

        GameResponse response = gameService.joinGame(request);

        // Validate response
        assertNotNull(response);
        assertEquals(TEST_GAME_ID, response.getGameId());
        assertEquals(GameStatus.LOBBY, response.getStatus());

        // Validate player data
        assertEquals(1, response.getPlayers().size());
        var playerDto = response.getPlayers().get(0);
        assertEquals(TEST_PLAYER_ID, playerDto.getPlayerId());
        assertEquals(TEST_PLAYER_NAME, playerDto.getPlayerName());
        assertFalse(playerDto.isReady());
    }

    /**
     * Ensures that a player who already joined a game is not added again.
     * Verifies that multiple join attempts by the same player do not result in duplicates.
     */
    @Test
    void testJoinGamePlayerAlreadyExistsNotAddedAgain() {
        GameRequest request = createDefaultGameRequest();

        gameService.joinGame(request);
        GameResponse response = gameService.joinGame(request);

        assertNotNull(response);
        assertEquals(1, response.getPlayers().size());
        assertEquals(TEST_PLAYER_ID, response.getPlayers().get(0).getPlayerId());
    }

    /**
     * Verifies that if the requesting player is not part of the game,
     * the response contains an empty handCards list (fallback behavior).
     */
    @Test
    void testJoinGameWithUnknownRequestingPlayerReturnsEmptyHandCards() {
        GameRequest request = createDefaultGameRequest();
        gameService.joinGame(request);

        GameRequest unknownPlayerRequest = new GameRequest();
        unknownPlayerRequest.setGameId(TEST_GAME_ID);
        unknownPlayerRequest.setPlayerId("unknownPlayer");
        unknownPlayerRequest.setPlayerName("ShouldNotBeAdded");

        GameResponse response = gameService.joinGame(unknownPlayerRequest);

        assertNotNull(response);
        assertEquals(TEST_GAME_ID, response.getGameId());
        assertTrue(response.getHandCards().isEmpty(), "HandCards should be empty for unknown player");
    }

    /**
     * Verifies that a player with an empty hand gets an empty card list in the response.
     * This covers the 'getHandCards().stream()' branch when the list is empty.
     */
    @Test
    void testJoinGamePlayerWithEmptyHandReturnsEmptyHandCards() {
        GameRequest request = createDefaultGameRequest();

        GameResponse response = gameService.joinGame(request);

        assertNotNull(response);
        assertEquals(TEST_GAME_ID, response.getGameId());
        assertEquals(1, response.getPlayers().size());
        assertEquals(TEST_PLAYER_ID, response.getPlayers().get(0).getPlayerId());
        assertTrue(response.getHandCards().isEmpty(), "Hand cards should be empty for new player");
    }

    private GameRequest createDefaultGameRequest() {
        GameRequest request = new GameRequest();
        request.setGameId(TEST_GAME_ID);
        request.setPlayerId(TEST_PLAYER_ID);
        request.setPlayerName(TEST_PLAYER_NAME);
        return request;
    }
}
