package rules;

import com.aau.wizard.model.CardFactory;
import com.aau.wizard.model.ICard;
import com.aau.wizard.model.Player;
import com.aau.wizard.model.enums.CardSuit;
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
        ICard wizard = CardFactory.createCard(CardSuit.RED, 14); // Wizard
        List<Pair<Player, ICard>> playedCards = List.of(new Pair<>(player, wizard));

        Player winner = TrickRules.determineTrickWinner(playedCards, CardSuit.BLUE);
        assertEquals(player, winner);
    }

    @Test
    void determineTrickWinner_singleJester_winsByDefault() {
        Player player = createTestPlayer("p1", "Player1");
        ICard jester = CardFactory.createCard(CardSuit.RED, 0); // Jester
        List<Pair<Player, ICard>> playedCards = List.of(new Pair<>(player, jester));

        Player winner = TrickRules.determineTrickWinner(playedCards, CardSuit.BLUE);
        assertEquals(player, winner);
    }

    @Test
    void determineTrickWinner_wizardBeatsAll() {
        Player player1 = createTestPlayer("p1", "Player1");
        Player player2 = createTestPlayer("p2", "Player2");
        Player player3 = createTestPlayer("p3", "Player3");

        List<Pair<Player, ICard>> playedCards = List.of(
                new Pair<>(player1, CardFactory.createCard(CardSuit.RED, 7)),
                new Pair<>(player2, CardFactory.createCard(CardSuit.BLUE, 10)),
                new Pair<>(player3, CardFactory.createCard(CardSuit.GREEN, 14)) // Wizard
        );

        Player winner = TrickRules.determineTrickWinner(playedCards, CardSuit.BLUE);
        assertEquals(player3, winner);
    }

    @Test
    void determineTrickWinner_trumpBeatsLeadSuit() {
        Player player1 = createTestPlayer("p1", "Player1");
        Player player2 = createTestPlayer("p2", "Player2");

        List<Pair<Player, ICard>> playedCards = List.of(
                new Pair<>(player1, CardFactory.createCard(CardSuit.RED, 10)),
                new Pair<>(player2, CardFactory.createCard(CardSuit.BLUE, 7)) // BLUE is trump
        );

        Player winner = TrickRules.determineTrickWinner(playedCards, CardSuit.BLUE);
        assertEquals(player2, winner);
    }

    @Test
    void determineTrickWinner_higherValueWinsInSameSuit() {
        Player player1 = createTestPlayer("p1", "Player1");
        Player player2 = createTestPlayer("p2", "Player2");

        List<Pair<Player, ICard>> playedCards = List.of(
                new Pair<>(player1, CardFactory.createCard(CardSuit.RED, 7)),
                new Pair<>(player2, CardFactory.createCard(CardSuit.RED, 10))
        );

        Player winner = TrickRules.determineTrickWinner(playedCards, CardSuit.BLUE);
        assertEquals(player2, winner);
    }

    @Test
    void determineTrickWinner_jesterDoesntAffectLeadSuit() {
        Player player1 = createTestPlayer("p1", "Player1");
        Player player2 = createTestPlayer("p2", "Player2");
        Player player3 = createTestPlayer("p3", "Player3");

        List<Pair<Player, ICard>> playedCards = List.of(
                new Pair<>(player1, CardFactory.createCard(CardSuit.RED, 0)), // Jester
                new Pair<>(player2, CardFactory.createCard(CardSuit.BLUE, 7)),
                new Pair<>(player3, CardFactory.createCard(CardSuit.BLUE, 10))
        );

        Player winner = TrickRules.determineTrickWinner(playedCards, CardSuit.GREEN);
        assertEquals(player3, winner);
    }

    @Test
    void isValidPlay_wizardAlwaysValid() {
        Player player = createTestPlayer("p1", "Player1");
        ICard wizard = CardFactory.createCard(CardSuit.RED, 14);
        CardSuit trump = CardSuit.BLUE;

        assertTrue(TrickRules.isValidPlay(player, wizard, List.of(), trump, false));
        assertTrue(TrickRules.isValidPlay(player, wizard,
                List.of(new Pair<>(player, CardFactory.createCard(CardSuit.RED, 7))), trump, false));
    }

    @Test
    void isValidPlay_jesterAlwaysValid() {
        Player player = createTestPlayer("p1", "Player1");
        ICard jester = CardFactory.createCard(CardSuit.RED, 0);
        CardSuit trump = CardSuit.RED;

        assertTrue(TrickRules.isValidPlay(player, jester, List.of(), trump, false));
        assertTrue(TrickRules.isValidPlay(player, jester,
                List.of(new Pair<>(player, CardFactory.createCard(CardSuit.RED, 7))), trump, false));
    }

    @Test
    void isValidPlay_firstPlayAlwaysValid() {
        Player player = createTestPlayer("p1", "Player1");
        ICard card = CardFactory.createCard(CardSuit.RED, 7);
        CardSuit trump = CardSuit.BLUE;

        assertTrue(TrickRules.isValidPlay(player, card, List.of(), trump, false));
    }

    @Test
    void isValidPlay_mustFollowSuitIfPossible() {
        Player player = createTestPlayer("p1", "Player1");
        player.setHandCards(List.of(
                CardFactory.createCard(CardSuit.RED, 7),
                CardFactory.createCard(CardSuit.BLUE, 8),
                CardFactory.createCard(CardSuit.GREEN, 9)
        ));

        List<Pair<Player, ICard>> currentTrick = List.of(
                new Pair<>(createTestPlayer("p2", "Player2"), CardFactory.createCard(CardSuit.RED, 10))
        );
        CardSuit trump = CardSuit.YELLOW;

        assertTrue(TrickRules.isValidPlay(player, CardFactory.createCard(CardSuit.RED, 5), currentTrick, trump, false));

        assertFalse(TrickRules.isValidPlay(player, CardFactory.createCard(CardSuit.BLUE, 5), currentTrick, trump, false));

        player.setHandCards(List.of(CardFactory.createCard(CardSuit.BLUE, 8)));
        assertTrue(TrickRules.isValidPlay(player, CardFactory.createCard(CardSuit.BLUE, 5), currentTrick, trump, false));
    }
    }
