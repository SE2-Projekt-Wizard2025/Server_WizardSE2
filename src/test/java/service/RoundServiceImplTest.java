package service;

import com.aau.wizard.model.Card;
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

    @BeforeEach
    void setUp() {
        players = List.of(
                new Player("p1", "Alice"),
                new Player("p2", "Bob"),
                new Player("p3", "Charlie")
        );
        roundService = new RoundServiceImpl(players);
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
        int cardsNeededForPlayers = players.size() * 3;
        int totalCardsInDeck = roundService.deck.size();
        int cardsToDraw = totalCardsInDeck - cardsNeededForPlayers - 1;

        roundService.deck.draw(cardsToDraw);

        roundService.startRound(3);

        assertNotNull(roundService.trumpCard);
        assertNotNull(roundService.trumpCardSuit);
    }

    @Test
    void playCard_validCard_playsSuccessfully() {
        roundService.startRound(3);
        Player player = players.get(0);
        Card card = player.getHandCards().get(0);

        roundService.playCard(player, card);

        assertEquals(1, roundService.playedCards.size());
        assertEquals(2, player.getHandCards().size());
    }

    @Test
    void playCard_invalidCard_throwsException() {
        roundService.startRound(3);
        Player player = players.get(0);
        Card invalidCard = new Card(CardSuit.BLUE, 5); // Not in player's hand

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
}