package dto.response;

import com.aau.wizard.dto.CardDto;
import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.model.enums.GameStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameResponseTest {

    private static final String GAME_ID = "game-123";
    private static final GameStatus STATUS = GameStatus.LOBBY;
    private static final String CURRENT_PLAYER_ID = "player1";
    private static final String LAST_PLAYED_CARD = "RED-5";

    private static final PlayerDto TEST_PLAYER = new PlayerDto("player1", "Alice", 0, false);
    private static final CardDto TEST_CARD = new CardDto("RED", "5", "NORMAL");

    /**
     * Verifies that the no-args constructor allows proper field setting and retrieval.
     */
    @Test
    void testNoArgsConstructorAndSetters() {
        GameResponse response = new GameResponse();
        response.setGameId(GAME_ID);
        response.setStatus(STATUS);
        response.setCurrentPlayerId(CURRENT_PLAYER_ID);
        response.setPlayers(List.of(TEST_PLAYER));
        response.setHandCards(List.of(TEST_CARD));
        response.setLastPlayedCard(LAST_PLAYED_CARD);

        assertEquals(GAME_ID, response.getGameId());
        assertEquals(STATUS, response.getStatus());
        assertEquals(CURRENT_PLAYER_ID, response.getCurrentPlayerId());
        assertEquals(1, response.getPlayers().size());
        assertEquals(TEST_PLAYER.getPlayerId(), response.getPlayers().get(0).getPlayerId());
        assertEquals(1, response.getHandCards().size());
        assertEquals(TEST_CARD.getColor(), response.getHandCards().get(0).getColor());
        assertEquals(LAST_PLAYED_CARD, response.getLastPlayedCard());
    }

    /**
     * Verifies that the all-args constructor correctly initializes all fields.
     */
    @Test
    void testAllArgsConstructor() {
        GameResponse response = new GameResponse(
                GAME_ID,
                STATUS,
                CURRENT_PLAYER_ID,
                List.of(TEST_PLAYER),
                List.of(TEST_CARD),
                LAST_PLAYED_CARD
        );

        assertEquals(GAME_ID, response.getGameId());
        assertEquals(STATUS, response.getStatus());
        assertEquals(CURRENT_PLAYER_ID, response.getCurrentPlayerId());
        assertEquals(1, response.getPlayers().size());
        assertEquals(TEST_PLAYER.getPlayerName(), response.getPlayers().get(0).getPlayerName());
        assertEquals(1, response.getHandCards().size());
        assertEquals(TEST_CARD.getValue(), response.getHandCards().get(0).getValue());
        assertEquals(LAST_PLAYED_CARD, response.getLastPlayedCard());
    }

    /**
     * Tests that GameResponse can be correctly deserialized from JSON using Jackson.
     */
    @Test
    void testJsonDeserialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "gameId": "game-123",
                  "status": "LOBBY",
                  "currentPlayerId": "player1",
                  "players": [
                    {
                      "playerId": "player1",
                      "playerName": "Alice",
                      "score": 0,
                      "ready": false
                    }
                  ],
                  "handCards": [
                    {
                      "color": "RED",
                      "value": "5",
                      "type": "NORMAL"
                    }
                  ],
                  "lastPlayedCard": "RED-5"
                }
                """;

        GameResponse response = mapper.readValue(json, GameResponse.class);

        assertEquals("game-123", response.getGameId());
        assertEquals(GameStatus.LOBBY, response.getStatus());
        assertEquals("player1", response.getCurrentPlayerId());
        assertEquals(1, response.getPlayers().size());
        assertEquals("Alice", response.getPlayers().get(0).getPlayerName());
        assertEquals(1, response.getHandCards().size());
        assertEquals("5", response.getHandCards().get(0).getValue());
        assertEquals("RED-5", response.getLastPlayedCard());
    }
}
