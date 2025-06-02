package dto.request;
import com.aau.wizard.dto.request.GameRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import static testutil.TestDataFactory.*;
import static testutil.TestConstants.*;

public class GameRequestTest {
    /**
     * Verifies that all fields can be set and retrieved correctly
     * when using the no-args constructor and setter methods.
     */
    @Test
    void testNoArgsConstructorAndSetters() {
        GameRequest request = createDefaultGameRequest();

        assertPopulatedGameRequest(request);
    }

    /**
     * Verifies that the constructor with gameId and playerId sets only those fields,
     * and the remaining fields remain null by default.
     */
    @Test
    void testConstructorWithGameIdAndPlayerId() {
        GameRequest request = new GameRequest(TEST_GAME_ID, TEST_PLAYER_ID);

        assertEquals(TEST_GAME_ID, request.getGameId());
        assertEquals(TEST_PLAYER_ID, request.getPlayerId());

        assertNull(request.getPlayerName());
        assertNull(request.getCard());
        assertNull(request.getAction());
    }

    /**
     * Verifies that setters correctly override existing field values.
     */
    @Test
    void testSettersOverrideValuesCorrectly() {
        GameRequest request = createDefaultGameRequest();

        request.setGameId("game-override");
        request.setPlayerId("player-override");

        assertEquals("game-override", request.getGameId());
        assertEquals("player-override", request.getPlayerId());
    }

    /**
     * Tests JSON deserialization using Jackson.
     * Ensures that all fields are correctly populated from a JSON string.
     */
    @Test
    void testJsonDeserialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        GameRequest request = mapper.readValue(VALID_GAME_REQUEST_JSON, GameRequest.class);

        assertPopulatedGameRequest(request);
    }

    /**
     * Asserts that the given {@link GameRequest} contains all expected standard test values.
     * <p>
     * Used to validate that a GameRequest was correctly populated via setters or deserialization.
     *
     * @param request the {@link GameRequest} instance to verify
     */
    private void assertPopulatedGameRequest(GameRequest request) {
        assertEquals(TEST_GAME_ID, request.getGameId());
        assertEquals(TEST_PLAYER_ID, request.getPlayerId());
        assertEquals(TEST_PLAYER_NAME, request.getPlayerName());
        assertEquals(TEST_CARD, request.getCard());
        assertEquals(TEST_ACTION, request.getAction());
    }
}
