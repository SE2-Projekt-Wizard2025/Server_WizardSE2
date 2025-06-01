package model;

import com.aau.wizard.model.*;
import com.aau.wizard.model.enums.CardSuit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeckUtilsTest {

    @Test
    void getTrumpSuit_returnsWizardSuit() {
        List<ICard> cards = List.of(
                CardFactory.createCard(CardSuit.RED, 5),
                CardFactory.createCard(CardSuit.BLUE, 14),
                CardFactory.createCard(CardSuit.GREEN, 7)
        );

        CardSuit trumpSuit = DeckUtils.getTrumpSuit(cards);
        assertEquals(CardSuit.BLUE, trumpSuit);
    }

    @Test
    void getTrumpSuit_returnsFirstWizardSuit() {
        List<ICard> cards = List.of(
                CardFactory.createCard(CardSuit.RED, 14),
                CardFactory.createCard(CardSuit.BLUE, 14),
                CardFactory.createCard(CardSuit.GREEN, 7)
        );

        CardSuit trumpSuit = DeckUtils.getTrumpSuit(cards);
        assertEquals(CardSuit.RED, trumpSuit);
    }

    @Test
    void getTrumpSuit_returnsNullWhenNoWizard() {
        List<ICard> cards = List.of(
                CardFactory.createCard(CardSuit.RED, 5),
                CardFactory.createCard(CardSuit.BLUE, 7),
                CardFactory.createCard(CardSuit.SPECIAL, 0)
        );

        CardSuit trumpSuit = DeckUtils.getTrumpSuit(cards);
        assertNull(trumpSuit);
    }

    @Test
    void getTrumpSuit_handlesEmptyList() {
        List<ICard> emptyList = List.of();
        CardSuit trumpSuit = DeckUtils.getTrumpSuit(emptyList);
        assertNull(trumpSuit);
    }

    @Test
    void isWizard_returnsTrueForWizard() {
        ICard wizard = CardFactory.createCard(CardSuit.SPECIAL, 14);
        assertTrue(WizardCard.isWizard(wizard));
    }

    @Test
    void isWizard_returnsFalseForNonWizard() {
        ICard numberCard = CardFactory.createCard(CardSuit.RED, 5);
        ICard jester = CardFactory.createCard(CardSuit.SPECIAL, 0);

        assertFalse(WizardCard.isWizard(numberCard));
        assertFalse(WizardCard.isWizard(jester));
    }

    @Test
    void isJester_returnsTrueForJester() {
        ICard jester = CardFactory.createCard(CardSuit.SPECIAL, 0);
        assertTrue(JesterCard.isJester(jester));
    }

    @Test
    void isJester_returnsFalseForNonJester() {
        ICard numberCard = CardFactory.createCard(CardSuit.BLUE, 7);
        ICard wizard = CardFactory.createCard(CardSuit.SPECIAL, 14);

        assertFalse(JesterCard.isJester(numberCard));
        assertFalse(JesterCard.isJester(wizard));
    }

    @Test
    void constructor_isPrivate() {
        assertThrows(IllegalAccessException.class, () -> {
            DeckUtils.class.getDeclaredConstructor().newInstance();
        });
    }
}