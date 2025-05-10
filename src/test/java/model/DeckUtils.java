package model;

import com.aau.wizard.model.Card;
import com.aau.wizard.model.DeckUtils;
import com.aau.wizard.model.enums.CardSuit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeckUtilsTest {

    @Test
    void getTrumpSuit_returnsWizardSuit() {
        List<Card> cards = List.of(
                new Card(CardSuit.RED, 5),
                new Card(CardSuit.BLUE, 14),
                new Card(CardSuit.GREEN, 7)
        );

        CardSuit trumpSuit = DeckUtils.getTrumpSuit(cards);
        assertEquals(CardSuit.BLUE, trumpSuit);
    }

    @Test
    void getTrumpSuit_returnsFirstWizardSuit() {
        List<Card> cards = List.of(
                new Card(CardSuit.RED, 14),
                new Card(CardSuit.BLUE, 14),
                new Card(CardSuit.GREEN, 7)
        );

        CardSuit trumpSuit = DeckUtils.getTrumpSuit(cards);
        assertEquals(CardSuit.RED, trumpSuit);
    }

    @Test
    void getTrumpSuit_returnsNullWhenNoWizard() {
        List<Card> cards = List.of(
                new Card(CardSuit.RED, 5),
                new Card(CardSuit.BLUE, 7),
                new Card(CardSuit.SPECIAL, 0)
        );

        CardSuit trumpSuit = DeckUtils.getTrumpSuit(cards);
        assertNull(trumpSuit);
    }

    @Test
    void getTrumpSuit_handlesEmptyList() {
        List<Card> emptyList = List.of();
        CardSuit trumpSuit = DeckUtils.getTrumpSuit(emptyList);
        assertNull(trumpSuit);
    }

    @Test
    void isWizard_returnsTrueForWizard() {
        Card wizard = new Card(CardSuit.SPECIAL, 14);
        assertTrue(DeckUtils.isWizard(wizard));
    }

    @Test
    void isWizard_returnsFalseForNonWizard() {
        Card numberCard = new Card(CardSuit.RED, 5);
        Card jester = new Card(CardSuit.SPECIAL, 0);

        assertFalse(DeckUtils.isWizard(numberCard));
        assertFalse(DeckUtils.isWizard(jester));
    }

    @Test
    void isJester_returnsTrueForJester() {
        Card jester = new Card(CardSuit.SPECIAL, 0);
        assertTrue(DeckUtils.isJester(jester));
    }

    @Test
    void isJester_returnsFalseForNonJester() {
        Card numberCard = new Card(CardSuit.BLUE, 7);
        Card wizard = new Card(CardSuit.SPECIAL, 14);

        assertFalse(DeckUtils.isJester(numberCard));
        assertFalse(DeckUtils.isJester(wizard));
    }

    @Test
    void constructor_isPrivate() {
        assertThrows(IllegalAccessException.class, () -> {
            DeckUtils.class.getDeclaredConstructor().newInstance();
        });
    }
}