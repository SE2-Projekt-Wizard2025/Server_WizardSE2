package service;

import com.aau.wizard.GameExceptions;
import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.request.PredictionRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.model.ICard;
import com.aau.wizard.model.Game;
import com.aau.wizard.model.Player;
import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.GameStatus;
import com.aau.wizard.service.impl.GameServiceImpl;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.*;

import com.aau.wizard.service.impl.RoundServiceImpl;
import com.aau.wizard.util.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static testutil.TestDataFactory.*;
import static testutil.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class GameServiceImplTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private GameServiceImpl gameService;

    private static final ICard TEST_CARD = createDefaultCard();

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
    /*@Test
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
    }*/

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

    private void givePlayerCard(String gameId, String playerId, ICard card) {
        Game game = gameService.getGameById(gameId);
        Player player = game.getPlayerById(playerId);
        player.setHandCards(List.of(card));
    }


    @Test
    void testStartGameSuccess() {
        // Spieler ins Spiel einfügen
        gameService.joinGame(createDefaultGameRequest()); // Player1
        gameService.joinGame(createCustomGameRequest(TEST_GAME_ID, "p2", "Player2"));
        gameService.joinGame(createCustomGameRequest(TEST_GAME_ID, "p3", "Player3"));

        // Spiel starten
        GameResponse response = gameService.startGame(TEST_GAME_ID);

        // Assertions zur Spiel-Response
        assertNotNull(response);
        assertEquals("PREDICTION", response.getStatus().name());

        List<String> playerIds = response.getPlayers().stream()
                .map(p -> p.getPlayerId())
                .toList();

        assertEquals(3, playerIds.size());
        assertTrue(playerIds.contains(response.getCurrentPlayerId()), "Current player must be in the list");

        for (String playerId : playerIds) {
            verify(messagingTemplate).convertAndSend(eq("/topic/game/" + playerId), any(GameResponse.class));
        }
    }

    @Test
    void testStartGameThrowsIfGameNotFound() {
        Exception exception = assertThrows(GameExceptions.GameNotFoundException.class, () -> {
            gameService.startGame("nonexistent-id");
        });


        assertTrue(exception.getMessage().contains("Spiel nicht gefunden"));
    }
    @Test
    void testStartGameFailsIfCannotStart() {

        gameService.joinGame(createDefaultGameRequest());

        Exception exception = assertThrows(GameExceptions.GameStartException.class, () -> {
            gameService.startGame(TEST_GAME_ID);
        });

        String expectedMessage = "Spiel konnte nicht gestartet werden";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testCanStartGameReturnsTrueIfAllowed() {
        gameService.joinGame(createDefaultGameRequest());
        gameService.joinGame(createCustomGameRequest(TEST_GAME_ID, "p2", "Player2"));
        gameService.joinGame(createCustomGameRequest(TEST_GAME_ID, "p3", "Player3"));

        assertTrue(gameService.canStartGame(TEST_GAME_ID));
    }

    @Test
    void testCanStartGameReturnsFalseIfGameDoesNotExist() {
        assertFalse(gameService.canStartGame("invalid-id"));
    }

    @Test
    void testGetGameByIdReturnsCorrectGame() {
        gameService.joinGame(createDefaultGameRequest());
        Game game = gameService.getGameById(TEST_GAME_ID);

        assertNotNull(game);
        assertEquals(TEST_GAME_ID, game.getGameId());
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

        game.setPredictionOrder(List.of(TEST_PLAYER_ID, "p2"));


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

    @Test
    void testMakePredictionThrowsExceptionWhenSumMatchesTotalTricks() { //wenn nur ein Spieler
        Game game = new Game(TEST_GAME_ID);
        Player player = new Player(TEST_PLAYER_ID, TEST_PLAYER_NAME);
        player.setHandCards(List.of(createDefaultCard()));
        game.setPlayers(List.of(player));

        game.setPredictionOrder(List.of(TEST_PLAYER_ID));

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

        assertThrows(GameExceptions.InvalidPredictionException.class, () -> {
            gameService.makePrediction(request);
        });
    }

    @Test
    void testPlayerCannotPredictOutOfTurn() throws Exception {
        Game game = new Game(TEST_GAME_ID);

        Player player1 = new Player("p1", "Anna");
        player1.setHandCards(List.of(createDefaultCard()));
        Player player2 = new Player("p2", "Ben");
        player2.setHandCards(List.of(createDefaultCard()));

        game.setPlayers(List.of(player1, player2));
        game.setPredictionOrder(List.of("p1", "p2")); // richtige Reihenfolge

        // Spieler 2 zu früh vorhersagen
        PredictionRequest invalidRequest = new PredictionRequest(TEST_GAME_ID, "p2", 1);


        Field gamesField = GameServiceImpl.class.getDeclaredField("games");
        gamesField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Game> gamesMap = (Map<String, Game>) gamesField.get(gameService);
        gamesMap.put(TEST_GAME_ID, game);


        Exception exception = assertThrows(GameExceptions.InvalidTurnException.class, () -> {
            gameService.makePrediction(invalidRequest);
        });

        assertEquals("Du bist noch nicht an der Reihe, bitte warte.", exception.getMessage());

    }

    @SuppressWarnings("unchecked")
    @Test
    void getScoreboard_shouldReturnCorrectDtoList() throws Exception {

        Game game = new Game("game-123");
        Player player = new Player("p1", "Alice");
        player.setPrediction(3);
        player.setTricksWon(3);
        player.setScore(50);
        game.getPlayers().add(player);

        GameServiceImpl service = new GameServiceImpl(mock(SimpMessagingTemplate.class));
        Field gamesField = GameServiceImpl.class.getDeclaredField("games");
        gamesField.setAccessible(true);
        Map<String, Game> gamesMap = (Map<String, Game>) gamesField.get(service);
        gamesMap.put("game-123", game);

        List<PlayerDto> scoreboard = service.getScoreboard("game-123");

        assertEquals(1, scoreboard.size());
        PlayerDto dto = scoreboard.get(0);
        assertEquals("Alice", dto.getPlayerName());
        assertEquals(3, dto.getPrediction());
        assertEquals(3, dto.getTricksWon());
        assertEquals(50, dto.getScore());
    }

    @Test
    void startGame_shouldInitializeRoundCountersCorrectly() throws Exception{
        Game game = new Game(TEST_GAME_ID);
        game.getPlayers().addAll(List.of(
                new Player("p1", "Alice"),
                new Player("p2", "Bob"),
                new Player("p3", "Charlie"),
                new Player("p4", "Dana")
        ));
                injectGameIntoService(game);
                gameService.startGame(TEST_GAME_ID);

        assertEquals(1, game.getCurrentRound(), "Die erste Runde sollte 1 sein.");
        assertEquals(15, game.getMaxRound(), "Bei 4 Spielern sollte es 15 Runden geben (60/4).");
    }

    @Test
    void processEndOfRound_shouldStartNextRound_whenGameIsNotOver() throws Exception{
        Game game = new Game(TEST_GAME_ID);
        game.getPlayers().addAll(List.of(new Player("p1", "Alice"), new Player("p2", "Bob"), new Player("p3", "Charlie")));
        game.setStatus(GameStatus.PLAYING);
        game.setCurrentRound(5);
        game.setMaxRound(20);
        injectGameIntoService(game);

        RoundServiceImpl mockRoundService = mock(RoundServiceImpl.class);
        injectRoundServiceIntoService(mockRoundService, game.getGameId());

        gameService.processEndOfRound(TEST_GAME_ID);

        assertEquals(6, game.getCurrentRound(), "Die Rundenzahl sollte auf 6 erhöht worden sein.");
        assertEquals(GameStatus.PLAYING, game.getStatus(), "Der Spielstatus sollte weiterhin PLAYING sein.");
        verify(mockRoundService, times(1)).startRound(6);

    }

    @Test
    void processEndOfRound_shouldEndGame_whenMayRoundReached() throws Exception{
        Game game = new Game(TEST_GAME_ID);
        game.getPlayers().addAll(List.of(new Player("p1", "Alice"), new Player("p2", "Bob"), new Player("p3", "Charlie")));
        game.setStatus(GameStatus.PLAYING);
        game.setCurrentRound(20);
        game.setMaxRound(20);
        injectGameIntoService(game);

        gameService.processEndOfRound(TEST_GAME_ID);

        assertEquals(GameStatus.ENDED, game.getStatus(), "Der Spielstatus sollte auf ENDED gesetzt sein.");

        for (Player player : game.getPlayers()) {
            verify(messagingTemplate).convertAndSend(eq("/topic/game/" + player.getPlayerId()), any(GameResponse.class));
        }
    }

    @Test
    void playCard_success_updatesCurrentPlayerAndBroadcasts() throws Exception {

        Game game = new Game(TEST_GAME_ID);
        Player player1 = new Player("p1", "Alice");
        Player player2 = new Player("p2", "Bob");
        Player player3 = new Player("p3", "Charlie");
        game.getPlayers().addAll(List.of(player1, player2, player3));
        game.setStatus(GameStatus.PLAYING);
        game.setCurrentRound(1);
        game.setMaxRound(1);
        game.setCurrentPlayerId(player1.getPlayerId());

        ICard cardToPlay = createCustomCard(CardSuit.RED, 7);
        player1.setHandCards(new ArrayList<>(List.of(cardToPlay, createDefaultCard())));

        injectGameIntoService(game);

        RoundServiceImpl realRoundService = new RoundServiceImpl(game, messagingTemplate, gameService);
        injectRoundServiceIntoService(realRoundService, TEST_GAME_ID);

        GameRequest request = new GameRequest(TEST_GAME_ID, player1.getPlayerId());
        request.setCard(cardToPlay.getSuit().name() + "_" + cardToPlay.getValue());


        GameResponse response = gameService.playCard(request);

        assertNotNull(response);
        assertEquals(TEST_GAME_ID, response.getGameId());
        assertEquals(GameStatus.PLAYING, response.getStatus());
        // Überprüfen, ob der nächste Spieler am Zug ist
        assertEquals(player2.getPlayerId(), response.getCurrentPlayerId());
        assertEquals(player2.getPlayerId(), game.getCurrentPlayerId());

        assertEquals(cardToPlay.toString(), response.getLastPlayedCard());

        verify(messagingTemplate).convertAndSend(eq("/topic/game/" + player2.getPlayerId()), any(GameResponse.class));
    }

    @Test
    void playCard_trickEnds_determinesWinnerAndResetsForNextTrick() throws Exception {

        Game game = new Game(TEST_GAME_ID);
        Player player1 = new Player("p1", "Alice");
        Player player2 = new Player("p2", "Bob");
        game.getPlayers().addAll(List.of(player1, player2));
        game.setStatus(GameStatus.PLAYING);
        game.setCurrentPlayerId(player1.getPlayerId());

        ICard card1 = createCustomCard(CardSuit.RED, 7);
        player1.setHandCards(new ArrayList<>(List.of(card1)));
        ICard card2 = createCustomCard(CardSuit.BLUE, 8);
        player2.setHandCards(new ArrayList<>(List.of(card2)));


        injectGameIntoService(game);
        RoundServiceImpl mockRoundService = mock(RoundServiceImpl.class);
        injectRoundServiceIntoService(mockRoundService, TEST_GAME_ID);

        when(mockRoundService.getPlayedCards()).thenReturn(List.of(new Pair<>(player1, card1)));

        // Simuliere den ersten Zug (Alice spielt)
        GameRequest request1 = new GameRequest(TEST_GAME_ID, player1.getPlayerId());
        String card1String = card1.getSuit().name() + "_" + card1.getValue();
        request1.setCard(card1String);
        gameService.playCard(request1);

        // Überprüfe den Zustand
        verify(mockRoundService).playCard(player1, card1, false);
        verify(mockRoundService, never()).endTrick();
        assertEquals("p2", game.getCurrentPlayerId(), "Nach Alice sollte Bob an der Reihe sein.");

        // Jetzt Bob

        when(mockRoundService.getPlayedCards()).thenReturn(List.of(new Pair<>(player1, card1), new Pair<>(player2, card2)));
        when(mockRoundService.endTrick()).thenReturn(player2); // Lege fest, dass Bob den Stich gewinnt.

        GameRequest request2 = new GameRequest(TEST_GAME_ID, player2.getPlayerId());
        String card2String = card2.getSuit().name() + "_" + card2.getValue();
        request2.setCard(card2String);
        gameService.playCard(request2);

        verify(mockRoundService).playCard(player2, card2, false);
        verify(mockRoundService, times(1)).endTrick();


        assertThat(game.getCurrentPlayerId())
                .withFailMessage("Der Gewinner (Bob) sollte den nächsten Stich beginnen.")
                .isEqualTo("p2");
    }

    @Test
    void playCard_playerNotActive_throwsException() throws Exception {
        Game game = new Game(TEST_GAME_ID);
        Player player1 = new Player("p1", "Alice");
        game.getPlayers().add(player1);
        game.setStatus(GameStatus.LOBBY); // nicht PLAYING
        game.setCurrentPlayerId(player1.getPlayerId()); // hinzufügen!

        injectGameIntoService(game);

        GameRequest request = new GameRequest(TEST_GAME_ID, player1.getPlayerId());
        request.setCard(createDefaultCard().toString());

        Exception exception = assertThrows(GameExceptions.GameNotActiveException.class, () -> {
            gameService.playCard(request);
        });

        System.out.println("Fehlermeldung war: " + exception.getMessage());

        assertTrue(exception.getMessage().contains("Das Spiel ist nicht aktiv oder wurde nicht gefunden."));
    }

    @Test
    void playCard_playerNotFound_throwsException() throws Exception {
        Game game = new Game(TEST_GAME_ID);
        game.setStatus(GameStatus.PLAYING);

        injectGameIntoService(game);

        GameRequest request = new GameRequest(TEST_GAME_ID, "unknownPlayer");
        request.setCard(createDefaultCard().toString());

        Exception exception = assertThrows(GameExceptions.PlayerNotFoundException.class, () -> {
            gameService.playCard(request);
        });

        assertTrue(exception.getMessage().contains("Spieler nicht gefunden."));}

    @Test
    void playCard_notPlayersTurn_throwsException() throws Exception {
        Game game = new Game(TEST_GAME_ID);
        Player player1 = new Player("p1", "Alice");
        Player player2 = new Player("p2", "Bob");
        game.getPlayers().addAll(List.of(player1, player2));
        game.setStatus(GameStatus.PLAYING);
        game.setCurrentPlayerId(player1.getPlayerId()); // Alice ist dran

        injectGameIntoService(game);

        GameRequest request = new GameRequest(TEST_GAME_ID, player2.getPlayerId()); // Bob versucht zu spielen
        request.setCard(createDefaultCard().toString());

        Exception exception = assertThrows(GameExceptions.InvalidTurnException.class, () -> {
            gameService.playCard(request);
        });

        assertTrue(exception.getMessage().contains("Du bist nicht an der Reihe."));
    }

    @Test
    void playCard_cardNotInHand_throwsException() throws Exception {
        Game game = new Game(TEST_GAME_ID);
        Player player1 = new Player("p1", "Alice");
        game.getPlayers().add(player1);
        game.setStatus(GameStatus.PLAYING);
        game.setCurrentPlayerId(player1.getPlayerId()); // Alice ist dran
        // Alice hat keine Karten oder nicht die gesuchte Karte
        player1.setHandCards(List.of(createCustomCard(CardSuit.BLUE, 5)));

        injectGameIntoService(game);
        injectRoundServiceIntoService(mock(RoundServiceImpl.class), TEST_GAME_ID);

        GameRequest request = new GameRequest(TEST_GAME_ID, player1.getPlayerId());
        ICard cardNotInHand = createCustomCard(CardSuit.RED, 7);
        String cardString = cardNotInHand.getSuit().name() + "_" + cardNotInHand.getValue();
        request.setCard(cardString);

        Exception exception = assertThrows(com.aau.wizard.GameExceptions.CardNotInHandException.class, () -> {
            gameService.playCard(request);
        });
        assertTrue(exception.getMessage().contains("Die Karte ist nicht in deiner Hand."));
    }
    @Test
    void playCard_roundServiceNotInitialized_throwsException() throws Exception {
        Game game = new Game(TEST_GAME_ID);
        Player player1 = new Player("p1", "Alice");
        game.getPlayers().add(player1);
        game.setStatus(GameStatus.PLAYING);
        game.setCurrentPlayerId(player1.getPlayerId());
        player1.setHandCards(List.of(createDefaultCard()));

        injectGameIntoService(game);

        GameRequest request = new GameRequest(TEST_GAME_ID, player1.getPlayerId());
        request.setCard(createDefaultCard().toString());

        Exception exception = assertThrows(com.aau.wizard.GameExceptions.RoundLogicException.class, () -> {
            gameService.playCard(request);
        });

        assertTrue(exception.getMessage().contains("Runden-Logik für dieses Spiel nicht gefunden."));}

    @SuppressWarnings("unchecked")
    private void injectGameIntoService(Game game) throws Exception {
        Field gamesField = GameServiceImpl.class.getDeclaredField("games");
        gamesField.setAccessible(true);
        Map<String, Game> gamesMap = (Map<String, Game>) gamesField.get(gameService);
        gamesMap.put(game.getGameId(), game);
    }

    @SuppressWarnings("unchecked")
    private void injectRoundServiceIntoService(RoundServiceImpl roundService, String gameId) throws Exception {
        Field roundServicesField = GameServiceImpl.class.getDeclaredField("roundServices");
        roundServicesField.setAccessible(true);
        Map<String, RoundServiceImpl> roundServicesMap = (Map<String, RoundServiceImpl>) roundServicesField.get(gameService);
        roundServicesMap.put(gameId, roundService);
    }

    @Test
    void playCard_invalidCardFormat_throwsException() throws Exception {
        Game game = new Game(TEST_GAME_ID);
        Player player1 = new Player("p1", "Alice");
        game.getPlayers().add(player1);
        game.setStatus(GameStatus.PLAYING);
        game.setCurrentPlayerId(player1.getPlayerId());
        player1.setHandCards(List.of(createCustomCard(CardSuit.RED, 7)));

        injectGameIntoService(game);
        injectRoundServiceIntoService(mock(RoundServiceImpl.class), TEST_GAME_ID);

        GameRequest request = new GameRequest(TEST_GAME_ID, player1.getPlayerId());
        request.setCard("INVALID_FORMAT");

        assertThrows(IllegalArgumentException.class, () -> {
            gameService.playCard(request);
        });
    }

    @Test
    void playCard_gameOver_throwsException() throws Exception {
        Game game = new Game(TEST_GAME_ID);
        Player player1 = new Player("p1", "Alice");
        game.getPlayers().add(player1);
        game.setStatus(GameStatus.ENDED); // Spiel ist beendet
        game.setCurrentPlayerId(player1.getPlayerId());
        player1.setHandCards(List.of(createDefaultCard()));

        injectGameIntoService(game);

        GameRequest request = new GameRequest(TEST_GAME_ID, player1.getPlayerId());
        request.setCard(createDefaultCard().toString());

        Exception exception = assertThrows(GameExceptions.GameAlreadyEndedException.class, () -> {
            gameService.playCard(request);
        });

        assertEquals("Das Spiel ist bereits beendet.", exception.getMessage());
    }

    @Test
    void playCard_gameNotFound_throwsException() {
        GameRequest request = new GameRequest("invalid_game_id", "p1");
        request.setCard(createDefaultCard().toString());

        Exception exception = assertThrows(GameExceptions.GameNotFoundException.class, () -> {
            gameService.playCard(request);
        });

        assertTrue(exception.getMessage().contains("Spiel nicht gefunden"));
    }

    @Test
    void playCard_playerHasNoCards_throwsException() throws Exception {
        Game game = new Game(TEST_GAME_ID);
        Player player1 = new Player("p1", "Alice");
        game.getPlayers().add(player1);
        game.setStatus(GameStatus.PLAYING);
        game.setCurrentPlayerId(player1.getPlayerId());
        player1.setHandCards(new ArrayList<>()); // Leere Hand

        injectGameIntoService(game);
        injectRoundServiceIntoService(mock(RoundServiceImpl.class), TEST_GAME_ID);

        GameRequest request = new GameRequest(TEST_GAME_ID, player1.getPlayerId());
        request.setCard(createDefaultCard().toString());

        Exception exception = assertThrows(com.aau.wizard.GameExceptions.CardNotInHandException.class, () -> {
            gameService.playCard(request);
        });

        assertTrue(exception.getMessage().contains("Die Karte ist nicht in deiner Hand."));
    }

    @Test
    void playCard_cardRemovedFromHand() throws Exception {
        Game game = new Game(TEST_GAME_ID);
        Player player1 = new Player("p1", "Alice");
        game.getPlayers().add(player1);
        game.setStatus(GameStatus.PLAYING);
        game.setCurrentPlayerId(player1.getPlayerId());

        ICard cardToPlay = createCustomCard(CardSuit.RED, 7);
        player1.setHandCards(new ArrayList<>(List.of(cardToPlay, createDefaultCard())));

        injectGameIntoService(game);
        RoundServiceImpl realRoundService = new RoundServiceImpl(game, messagingTemplate, gameService);
        injectRoundServiceIntoService(realRoundService, TEST_GAME_ID);

        GameRequest request = new GameRequest(TEST_GAME_ID, player1.getPlayerId());
        request.setCard(cardToPlay.getSuit().name() + "_" + cardToPlay.getValue());

        gameService.playCard(request);

        assertFalse(player1.getHandCards().contains(cardToPlay), "Gespielte Karte sollte aus der Hand entfernt sein.");
    }
    }








