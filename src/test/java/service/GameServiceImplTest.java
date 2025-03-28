package service;

import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.service.impl.GameServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class GameServiceImplTest {
    @InjectMocks
    private GameServiceImpl gameService;

    // TODO: Add further test attributes later on
    private static final String TEST_GAME_ID = "12345";

    @Test
    public void testStartGameSuccess() {
        GameRequest request = createDefaultGameRequest();

        GameResponse response = gameService.startGame(request);

        assertNotNull(response);
        assertNotNull(response.getGameId(), "Game ID should not be null");
    }

    private GameRequest createDefaultGameRequest() {
        // TODO: Add further attributes later on
        return new GameRequest(TEST_GAME_ID);
    }
}
