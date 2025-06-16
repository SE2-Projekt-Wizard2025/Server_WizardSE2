package dto.response;

import com.aau.wizard.dto.CardDto;
import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.dto.response.GameResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


import static org.junit.jupiter.api.Assertions.*;
import static testutil.TestDataFactory.*;
import static testutil.TestConstants.*;

class GameResponseTest {
    private PlayerDto testPlayer;
    private CardDto testCard;

    @BeforeEach
    void setup() {
        testPlayer = createDefaultPlayerDto();
        testCard = createDefaultCardDto();
    }

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
        PlayerDto player2 = createCustomPlayerDto("Player2", "SecondPlayer", 0, true);
        GameResponse response = createDefaultGameResponse(testPlayer, player2);

        assertThat(response).isNotNull();
        assertEquals(TEST_GAME_ID, response.getGameId());
        assertEquals(2, response.getPlayers().size());
        assertEquals(testPlayer.getPlayerId(), response.getPlayers().get(0).getPlayerId());
        assertEquals(player2.getPlayerId(), response.getPlayers().get(1).getPlayerId());
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
        assertEquals(TEST_GAME_ID, response.getGameId());
        assertEquals(TEST_GAME_STATUS, response.getStatus());
        assertEquals(TEST_PLAYER_ID, response.getCurrentPlayerId());
        assertEquals(2, response.getPlayers().size());
        assertEquals(testPlayer.getPlayerName(), response.getPlayers().get(0).getPlayerName());
        assertEquals(2, response.getHandCards().size());
        assertEquals(testCard.getValue(), response.getHandCards().get(0).getValue());
        assertEquals(TEST_LAST_PLAYED_CARD, response.getLastPlayedCard());
    }

    private GameResponse createGameResponseNoArgsConstructor() {
        GameResponse response = new GameResponse();
        response.setGameId(TEST_GAME_ID);
        response.setStatus(TEST_GAME_STATUS);
        response.setCurrentPlayerId(TEST_PLAYER_ID);
        response.setPlayers(createDefaultListOfPlayerDto());
        response.setHandCards(createDefaultListOfCardDto());
        response.setLastPlayedCard(TEST_LAST_PLAYED_CARD);
        return response;
    }
}
