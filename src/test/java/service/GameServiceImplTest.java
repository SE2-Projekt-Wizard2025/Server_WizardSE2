package service;

import com.aau.wizard.dto.CardDto;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.request.PredictionRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.model.Card;
import com.aau.wizard.model.Game;
import com.aau.wizard.model.Player;
import com.aau.wizard.service.impl.GameServiceImpl;
import static com.aau.wizard.testutil.TestConstants.*;
import static com.aau.wizard.testutil.TestDataFactory.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceImplTest {

    @InjectMocks
    private GameServiceImpl gameService;

    private static final Card TEST_CARD = createDefaultCard();

    /**
     * Verifies that a new game is created and a player is added when a player joins
     * a non-existent game. Also checks that the game state is returned correctly.
     */
    @Test
    void testJoinGameCreatesGameAndAddsPlayer() {
        GameRequest request = createDefaultGameRequest();

        GameResponse response = gameService.joinGame(request);

        assertBasicJoinResponse(response);
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

        assertBasicJoinResponse(response);
    }

    /**
     * Verifies that if the requesting player is not part of the game,
     * the response contains an empty handCards list (fallback behavior).
     */
    @Test
    void testJoinGameWithUnknownRequestingPlayerReturnsEmptyHandCards() {
        GameRequest request = createDefaultGameRequest();
        gameService.joinGame(request);

        GameRequest unknownPlayerRequest = createCustomGameRequest(
                TEST_GAME_ID,
                "unknownPlayer",
                "ShouldNotBeAdded"
        );

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

        assertBasicJoinResponse(response);
        assertTrue(response.getHandCards().isEmpty(), "Hand cards should be empty for new player");
    }

    /**
     * Verifies that the joinGame method correctly maps a player's hand cards
     * using CardDto.from(...) when the player already exists and has at least one card.
     * <p>
     * This test ensures that the internal stream().map(...) logic is actually executed.
     */
    @Test
    void testJoinGameWithPlayerAndCard() {
        GameRequest request = createDefaultGameRequest();
        gameService.joinGame(request);

        givePlayerCard(TEST_GAME_ID, TEST_PLAYER_ID, TEST_CARD);

        GameResponse response = gameService.joinGame(request);

        assertNotNull(response);
        assertEquals(1, response.getHandCards().size());

        CardDto card = response.getHandCards().get(0);
        assertEquals(TEST_CARD_COLOR, card.getColor());
        assertEquals(TEST_CARD_VALUE, card.getValue());
        assertEquals(TEST_CARD_TYPE, card.getType());
    }

    /**
     * Asserts that the given {@link GameResponse} contains the expected basic join information.
     * <p>
     * Validates that:
     * <ul>
     *     <li>the response is not null</li>
     *     <li>the game ID matches the test ID</li>
     *     <li>exactly one player is present</li>
     *     <li>the player's ID and name match the expected test values</li>
     * </ul>
     *
     * @param response the {@link GameResponse} to verify
     */
    private void assertBasicJoinResponse(GameResponse response) {
        assertNotNull(response);
        assertEquals(TEST_GAME_ID, response.getGameId());
        assertEquals(1, response.getPlayers().size());
        assertEquals(TEST_PLAYER_ID, response.getPlayers().get(0).getPlayerId());
        assertEquals(TEST_PLAYER_NAME, response.getPlayers().get(0).getPlayerName());
    }

    private void givePlayerCard(String gameId, String playerId, Card card) {
        Game game = gameService.getGameById(gameId);
        Player player = game.getPlayerById(playerId);
        player.setHandCards(List.of(card));
    }

    @Test
    void testMakePredictionStoresPrediction() {

        Game game = new Game(TEST_GAME_ID);

        // zwei spieler damit es nicht nur einen letzten spieler gibt
        Player player = new Player(TEST_PLAYER_ID, TEST_PLAYER_NAME);
        player.setHandCards(List.of(createDefaultCard()));

        Player other = new Player("p2", "Zweiter");
        other.setHandCards(List.of(createDefaultCard()));

        game.setPlayers(List.of(player, other));

        try {
            Field gamesField = GameServiceImpl.class.getDeclaredField("games");
            gamesField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, Game> gamesMap = (Map<String, Game>) gamesField.get(gameService);
            gamesMap.put(TEST_GAME_ID, game);
        } catch (Exception e) {
            fail("Fehler beim Zugriff auf games-Feld: " + e.getMessage());
        }


        PredictionRequest request = new PredictionRequest(TEST_GAME_ID, TEST_PLAYER_ID, 1);
        GameResponse response = gameService.makePrediction(request);


        assertNotNull(response);
        assertEquals(1, player.getPrediction());          // Vorhersage korrekt speichern
        assertEquals(TEST_GAME_ID, response.getGameId());
    }

}
