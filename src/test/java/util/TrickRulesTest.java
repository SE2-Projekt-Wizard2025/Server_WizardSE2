package util;

import com.aau.wizard.model.Card;
import com.aau.wizard.model.Player;
import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.CardType;
import com.aau.wizard.util.Pair;
import com.aau.wizard.util.TrickRules;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrickRulesTest {

    private Player createTestPlayer(String id, String name) {
        return new Player(id, name);
    }

    @Test
    void determineTrickWinner_emptyList_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> TrickRules.determineTrickWinner(List.of(), CardSuit.RED));
    }

    @Test
    void determineTrickWinner_singleWizard_wins() {
        Player player = createTestPlayer("p1", "Player1");
        Card wizard = new Card(CardSuit.RED, 14); // Wizard
        List<Pair<Player, Card>> playedCards = List.of(new Pair<>(player, wizard));

        Player winner = TrickRules.determineTrickWinner(playedCards, CardSuit.BLUE);
        assertEquals(player, winner);
    }

    @Test
    void determineTrickWinner_singleJester_winsByDefault() {
        Player player = createTestPlayer("p1", "Player1");
        Card jester = new Card(CardSuit.RED, 0); // Jester
        List<Pair<Player, Card>> playedCards = List.of(new Pair<>(player, jester));

        Player winner = TrickRules.determineTrickWinner(playedCards, CardSuit.BLUE);
        assertEquals(player, winner);
    }

    @Test
    void determineTrickWinner_wizardBeatsAll() {
        Player player1 = createTestPlayer("p1", "Player1");
        Player player2 = createTestPlayer("p2", "Player2");
        Player player3 = createTestPlayer("p3", "Player3");

        List<Pair<Player, Card>> playedCards = List.of(
                new Pair<>(player1, new Card(CardSuit.RED, 7)),
                new Pair<>(player2, new Card(CardSuit.BLUE, 10)),
                new Pair<>(player3, new Card(CardSuit.GREEN, 14)) // Wizard
        );

        Player winner = TrickRules.determineTrickWinner(playedCards, CardSuit.BLUE);
        assertEquals(player3, winner);
    }

    @Test
    void determineTrickWinner_trumpBeatsLeadSuit() {
        Player player1 = createTestPlayer("p1", "Player1");
        Player player2 = createTestPlayer("p2", "Player2");

        List<Pair<Player, Card>> playedCards = List.of(
                new Pair<>(player1, new Card(CardSuit.RED, 10)),
                new Pair<>(player2, new Card(CardSuit.BLUE, 7)) // BLUE is trump
        );

        Player winner = TrickRules.determineTrickWinner(playedCards, CardSuit.BLUE);
        assertEquals(player2, winner);
    }

    @Test
    void determineTrickWinner_higherValueWinsInSameSuit() {
        Player player1 = createTestPlayer("p1", "Player1");
        Player player2 = createTestPlayer("p2", "Player2");

        List<Pair<Player, Card>> playedCards = List.of(
                new Pair<>(player1, new Card(CardSuit.RED, 7)),
                new Pair<>(player2, new Card(CardSuit.RED, 10))
        );

        Player winner = TrickRules.determineTrickWinner(playedCards, CardSuit.BLUE);
        assertEquals(player2, winner);
    }

    @Test
    void determineTrickWinner_jesterDoesntAffectLeadSuit() {
        Player player1 = createTestPlayer("p1", "Player1");
        Player player2 = createTestPlayer("p2", "Player2");
        Player player3 = createTestPlayer("p3", "Player3");

        List<Pair<Player, Card>> playedCards = List.of(
                new Pair<>(player1, new Card(CardSuit.RED, 0)), // Jester
                new Pair<>(player2, new Card(CardSuit.BLUE, 7)),
                new Pair<>(player3, new Card(CardSuit.BLUE, 10))
        );

        Player winner = TrickRules.determineTrickWinner(playedCards, CardSuit.GREEN);
        assertEquals(player3, winner);
    }

    @Test
    void isValidPlay_wizardAlwaysValid() {
        Player player = createTestPlayer("p1", "Player1");
        Card wizard = new Card(CardSuit.RED, 14);

        assertTrue(TrickRules.isValidPlay(player, wizard, List.of()));
        assertTrue(TrickRules.isValidPlay(player, wizard,
                List.of(new Pair<>(player, new Card(CardSuit.BLUE, 7)))));
    }

    @Test
    void isValidPlay_jesterAlwaysValid() {
        Player player = createTestPlayer("p1", "Player1");
        Card jester = new Card(CardSuit.RED, 0);

        assertTrue(TrickRules.isValidPlay(player, jester, List.of()));
        assertTrue(TrickRules.isValidPlay(player, jester,
                List.of(new Pair<>(player, new Card(CardSuit.BLUE, 7)))));
    }

    @Test
    void isValidPlay_firstPlayAlwaysValid() {
        Player player = createTestPlayer("p1", "Player1");
        Card card = new Card(CardSuit.RED, 7);

        assertTrue(TrickRules.isValidPlay(player, card, List.of()));
    }

    @Test
    void isValidPlay_mustFollowSuitIfPossible() {
        Player player = createTestPlayer("p1", "Player1");
        player.setHandCards(List.of(
                new Card(CardSuit.RED, 7),
                new Card(CardSuit.BLUE, 8),
                new Card(CardSuit.GREEN, 9)
        ));

        List<Pair<Player, Card>> currentTrick = List.of(
                new Pair<>(createTestPlayer("p2", "Player2"), new Card(CardSuit.RED, 10))
        );

        assertTrue(TrickRules.isValidPlay(player, new Card(CardSuit.RED, 5), currentTrick));

        assertFalse(TrickRules.isValidPlay(player, new Card(CardSuit.BLUE, 5), currentTrick));

        player.setHandCards(List.of(new Card(CardSuit.BLUE, 8)));
        assertTrue(TrickRules.isValidPlay(player, new Card(CardSuit.BLUE, 5), currentTrick));
    }
}