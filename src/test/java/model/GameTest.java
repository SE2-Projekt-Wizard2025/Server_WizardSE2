package model;

import com.aau.wizard.model.Game;
import com.aau.wizard.model.Player;
import com.aau.wizard.model.enums.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import static com.aau.wizard.testutil.TestConstants.*;
import static com.aau.wizard.testutil.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;
    private List<Player> testPlayers;

    @BeforeEach
    void setUp() {
        game = createDefaultGame();
        testPlayers = createDefaultListOfPlayer();
    }

    @Test
    void testConstructorAndDefaultStatus() {
        assertStandardGameValues();
    }

    @Test
    void testSetAndGetGameId() {
        game.setGameId("newGame");
        assertEquals("newGame", game.getGameId());
    }

    @Test
    void testSetAndGetPlayers() {
        game.setPlayers(testPlayers);
        assertEquals(2, game.getPlayers().size());
        assertEquals(TEST_PLAYER_ID, game.getPlayers().get(0).getPlayerId());
    }

    @Test
    void testSetAndGetCurrentPlayerId() {
        game.setCurrentPlayerId("p1");
        assertEquals("p1", game.getCurrentPlayerId());
    }

    @Test
    void testSetAndGetStatus() {
        game.setStatus(GameStatus.PLAYING);
        assertEquals(GameStatus.PLAYING, game.getStatus());
    }

    @Test
    void testGetPlayerByIdPlayerExists() {
        game.setPlayers(testPlayers);
        Player found = game.getPlayerById(TEST_PLAYER_ID);
        assertNotNull(found);
        assertEquals(TEST_PLAYER_NAME, found.getName());
    }

    @Test
    void testGetPlayerByIdPlayerNotExists() {
        game.setPlayers(testPlayers);
        Player found = game.getPlayerById("nonexistent");
        assertNull(found);
    }

    private void assertStandardGameValues() {
        assertEquals(TEST_GAME_ID, game.getGameId());
        assertEquals(TEST_GAME_STATUS, game.getStatus());
        assertNotNull(game.getPlayers());
        assertTrue(game.getPlayers().isEmpty());
    }
}
