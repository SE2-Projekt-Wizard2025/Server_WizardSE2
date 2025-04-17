package service;

import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.response.GameResponse;
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

    @Test
    void testJoinGameCreatesGameAndAddsPlayer() {
        GameRequest request = createDefaultGameRequest();

        GameResponse response = gameService.joinGame(request);

        assertNotNull(response);
        assertEquals(TEST_GAME_ID, response.getGameId());
        assertEquals(GameStatus.LOBBY, response.getStatus());
        assertEquals(1, response.getPlayers().size());

        var playerDto = response.getPlayers().get(0);
        assertEquals(TEST_PLAYER_ID, playerDto.getPlayerId());
        assertEquals(TEST_PLAYER_NAME, playerDto.getPlayerName());
        assertFalse(playerDto.isReady());
    }

    private GameRequest createDefaultGameRequest() {
        GameRequest request = new GameRequest();
        request.setGameId(TEST_GAME_ID);
        request.setPlayerId(TEST_PLAYER_ID);
        request.setPlayerName(TEST_PLAYER_NAME);
        return request;
    }
}
