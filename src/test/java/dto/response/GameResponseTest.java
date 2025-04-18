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

    private static final PlayerDto TEST_PLAYER = createDefaultPlayerDto();
    private static final CardDto TEST_CARD = createDefaultCardDto();

    private static final String VALID_GAME_RESPONSE_JSON = """
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

    /**
     * Verifies that the no-args constructor allows proper field setting and retrieval.
     */
    @Test
    void testNoArgsConstructorAndSetters() {
        GameResponse response = createGameResponseNoArgsConstructor();
        assertGameResponse(response);
    }

    /**
     * Verifies that the all-args constructor correctly initializes all fields.
     */
    @Test
    void testAllArgsConstructor() {
        GameResponse response = createGameResponseArgsConstructor();

        assertGameResponse(response);
    }

    /**
     * Tests that GameResponse can be correctly deserialized from JSON using Jackson.
     */
    @Test
    void testJsonDeserialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        GameResponse response = mapper.readValue(VALID_GAME_RESPONSE_JSON, GameResponse.class);

        assertGameResponse(response);
    }

    /**
     * Asserts that the given {@link GameResponse} contains all expected test values.
     * <p>
     * This includes the game ID, status, current player ID, player list, hand cards,
     * and the last played card. The assertions use static test constants defined in the test class.
     *
     * @param response the {@link GameResponse} to validate
     */
    private void assertGameResponse(GameResponse response) {
        assertNotNull(response);
        assertEquals(GAME_ID, response.getGameId());
        assertEquals(STATUS, response.getStatus());
        assertEquals(CURRENT_PLAYER_ID, response.getCurrentPlayerId());
        assertEquals(1, response.getPlayers().size());
        assertEquals(TEST_PLAYER.getPlayerName(), response.getPlayers().get(0).getPlayerName());
        assertEquals(1, response.getHandCards().size());
        assertEquals(TEST_CARD.getValue(), response.getHandCards().get(0).getValue());
        assertEquals(LAST_PLAYED_CARD, response.getLastPlayedCard());
    }

    private GameResponse createGameResponseNoArgsConstructor() {
        GameResponse response = new GameResponse();
        response.setGameId(GAME_ID);
        response.setStatus(STATUS);
        response.setCurrentPlayerId(CURRENT_PLAYER_ID);
        response.setPlayers(List.of(TEST_PLAYER));
        response.setHandCards(List.of(TEST_CARD));
        response.setLastPlayedCard(LAST_PLAYED_CARD);
        return response;
    }

    private GameResponse createGameResponseArgsConstructor() {
        return new GameResponse(
                GAME_ID,
                STATUS,
                CURRENT_PLAYER_ID,
                List.of(TEST_PLAYER),
                List.of(TEST_CARD),
                LAST_PLAYED_CARD
        );
    }

    private static PlayerDto createDefaultPlayerDto() {
        return new PlayerDto("player1", "Alice", 0, false);
    }

    private static CardDto createDefaultCardDto() {
        return new CardDto("RED", "5", "NORMAL");
    }
}
