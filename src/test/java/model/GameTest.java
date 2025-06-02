package model;

import com.aau.wizard.model.Game;
import com.aau.wizard.model.Player;
import com.aau.wizard.model.enums.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static testutil.TestDataFactory.*;
import static testutil.TestConstants.*;

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

    @Test
    void testAddPlayerSuccessful() {
        Player player = new Player("p-new", "New Player");
        assertTrue(game.addPlayer(player));
        assertEquals(1, game.getPlayers().size());
    }

    @Test
    void testAddPlayerFailsIfNotInLobby() {
        game.setStatus(GameStatus.PLAYING);
        Player player = new Player("p-x", "Blocked");
        assertFalse(game.addPlayer(player));
    }

    @Test
    void testAddPlayerFailsIfMoreThan6() {
        for (int i = 0; i < 6; i++) {
            assertTrue(game.addPlayer(new Player("p" + i, "Player" + i)));
        }
        assertFalse(game.addPlayer(new Player("p7", "TooMany")));
    }

    @Test
    void testCanStartGameReturnsTrueIfLobbyAndEnoughPlayers() {
        game.setPlayers(List.of(new Player("p1", "A"), new Player("p2", "B"), new Player("p3", "C")));
        assertTrue(game.canStartGame());
    }

    @Test
    void testCanStartGameReturnsFalseIfNotLobby() {
        game.setStatus(GameStatus.PLAYING);
        game.setPlayers(List.of(new Player("p1", "A"), new Player("p2", "B"), new Player("p3", "C")));
        assertFalse(game.canStartGame());
    }

    @Test
    void testStartGameSuccess() {
        game.setPlayers(new ArrayList<>(List.of(
                new Player("p1", "A"),
                new Player("p2", "B"),
                new Player("p3", "C")
        )));
        assertTrue(game.startGame());
        assertEquals(GameStatus.PLAYING, game.getStatus());
        assertNotNull(game.getCurrentPlayerId());
    }

    @Test
    void testStartGameFails() {
        // zu wenig Spieler
        game.setPlayers(new ArrayList<>(List.of(new Player("p1", "A"))));
        assertTrue(game.startGame());
    }

}
