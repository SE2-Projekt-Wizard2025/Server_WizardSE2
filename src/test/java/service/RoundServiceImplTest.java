package service;

import com.aau.wizard.model.CardFactory;
import com.aau.wizard.model.ICard;
import com.aau.wizard.model.Game;
import com.aau.wizard.model.Player;
import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.CardType;
import com.aau.wizard.service.impl.RoundServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoundServiceImplTest {

    private List<Player> players;
    private RoundServiceImpl roundService;
    private Game game;

    @BeforeEach
    void setUp() {
        players = List.of(
                new Player("p1", "Alice"),
                new Player("p2", "Bob"),
                new Player("p3", "Charlie")
            );

            game = new Game("test-game");
            game.setPlayers(players);

            roundService = new RoundServiceImpl(game);
        }

    @Test
    void startRound_initializesGameStateCorrectly() {
        roundService.startRound(3);

        // Verify each player got 3 cards
        players.forEach(p -> assertEquals(3, p.getHandCards().size()));

        // Verify game state reset
        assertEquals(0, roundService.currentTrickNumber);
        assertTrue(roundService.playedCards.isEmpty());
    }

    @Test
    void startRound_setsTrumpCardWhenDeckHasCardsAfterDealing() {
        Game games = new Game("123");
        games.addPlayer(new Player("Player1", "Alice"));
        games.addPlayer(new Player("Player2", "Bob"));

        RoundServiceImpl roundServiceImp = new RoundServiceImpl(games);


        // Runde starten → deck wird hier initialisiert
        roundServiceImp.startRound(3);


        assertNotNull(roundServiceImp.trumpCard, "Trumpfkarte sollte gesetzt sein");
        assertNotNull(roundServiceImp.trumpCardSuit, "Trumpffarbe sollte gesetzt sein");
    }

        @Test
    void playCard_validCard_playsSuccessfully() {
        roundService.startRound(3);
        Player player = players.get(0);
        ICard card = player.getHandCards().get(0);

        roundService.playCard(player, card);

        assertEquals(1, roundService.playedCards.size());
        assertEquals(2, player.getHandCards().size());
    }

    @Test
    void playCard_invalidCard_throwsException() {
        roundService.startRound(3);
        Player player = players.get(0);
        ICard invalidCard = CardFactory.createCard(CardSuit.BLUE, 5); // Not in player's hand

        assertThrows(IllegalArgumentException.class,
                () -> roundService.playCard(player, invalidCard));
    }


    @Test
    void endTrick_noCardsPlayed_throwsException() {
        roundService.startRound(3);
        assertThrows(IllegalStateException.class, () -> roundService.endTrick());
    }

    @Test
    void endRound_calculatesScoresCorrectly() {
        // Setup round with bids and tricks won
        roundService.startRound(3);
        players.get(0).setBid(2); players.get(0).setTricksWon(2); // Perfect
        players.get(1).setBid(1); players.get(1).setTricksWon(3); // Under by 2
        players.get(2).setBid(3); players.get(2).setTricksWon(1); // Over by 2

        roundService.endRound();

        assertEquals(40, players.get(0).getScore());  // 20 + 2*10
        assertEquals(10, players.get(1).getScore());  // 3*10 - 2*10
        assertEquals(-10, players.get(2).getScore()); // 1*10 - 2*10
    }

    @Test
    void endRound_setsPredictionOrderStartingWithWinner() {
        // Charlie gewinnt
        players.get(0).setBid(1); players.get(0).setTricksWon(1);
        players.get(1).setBid(2); players.get(1).setTricksWon(2);
        players.get(2).setBid(3); players.get(2).setTricksWon(4);

        roundService.endRound();

        List<String> predictionOrder = game.getPredictionOrder();
        assertNotNull(predictionOrder);
        assertEquals(3, predictionOrder.size());
        assertEquals("p3", predictionOrder.get(0)); // Charlie = Gewinner
        assertTrue(predictionOrder.containsAll(List.of("p1", "p2", "p3")));
    }

    @Test
    void startRound_resetsPredictions() {
        players.forEach(p -> p.setPrediction(2));

        roundService.startRound(3);

        players.forEach(p -> assertNull(p.getPrediction(), "Prediction sollte zurückgesetzt sein"));
    }


}