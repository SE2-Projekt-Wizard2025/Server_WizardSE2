package dto.request;
import com.aau.wizard.dto.request.GameRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameRequestTest {

    private static final String GAME_ID = "game-1";
    private static final String PLAYER_ID = "player-1";
    private static final String PLAYER_NAME = "TestPlayer";
    private static final String CARD = "RED-7";
    private static final String ACTION = "JOIN";

    /**
     * Verifies that all fields can be set and retrieved correctly
     * when using the no-args constructor and setter methods.
     */
    @Test
    void testNoArgsConstructorAndSetters() {
        GameRequest request = createPopulatedGameRequest();

        assertEquals(GAME_ID, request.getGameId());
        assertEquals(PLAYER_ID, request.getPlayerId());
        assertEquals(PLAYER_NAME, request.getPlayerName());
        assertEquals(CARD, request.getCard());
        assertEquals(ACTION, request.getAction());
    }

    /**
     * Verifies that the constructor with gameId and playerId sets only those fields,
     * and the remaining fields remain null by default.
     */
    @Test
    void testConstructorWithGameIdAndPlayerId() {
        GameRequest request = new GameRequest(GAME_ID, PLAYER_ID);

        assertEquals(GAME_ID, request.getGameId());
        assertEquals(PLAYER_ID, request.getPlayerId());

        assertNull(request.getPlayerName());
        assertNull(request.getCard());
        assertNull(request.getAction());
    }

    /**
     * Verifies that setters correctly override existing field values.
     */
    @Test
    void testSettersOverrideValuesCorrectly() {
        GameRequest request = new GameRequest(GAME_ID, PLAYER_ID);

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
        String json = createGameRequestJson();

        GameRequest request = mapper.readValue(json, GameRequest.class);

        assertEquals("game-json", request.getGameId());
        assertEquals("player-json", request.getPlayerId());
        assertEquals("JSONTest", request.getPlayerName());
        assertEquals("BLUE-5", request.getCard());
        assertEquals("PLAY", request.getAction());
    }

    /**
     * Helper method to create a fully populated GameRequest instance using constants.
     *
     * @return a populated GameRequest
     */
    private GameRequest createPopulatedGameRequest() {
        GameRequest request = new GameRequest();
        request.setGameId(GAME_ID);
        request.setPlayerId(PLAYER_ID);
        request.setPlayerName(PLAYER_NAME);
        request.setCard(CARD);
        request.setAction(ACTION);
        return request;
    }

    /**
     * Helper method to generate a sample JSON string for deserialization testing.
     *
     * @return a valid GameRequest JSON string
     */
    private String createGameRequestJson() {
        return """
                {
                  "gameId": "game-json",
                  "playerId": "player-json",
                  "playerName": "JSONTest",
                  "card": "BLUE-5",
                  "action": "PLAY"
                }
                """;
    }
}
