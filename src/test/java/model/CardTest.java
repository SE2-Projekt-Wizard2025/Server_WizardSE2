package model;

import com.aau.wizard.model.CardFactory;
import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.ICard;
import com.aau.wizard.model.enums.CardType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testNumberCardCreation() {
        ICard card = CardFactory.createCard(CardSuit.RED, 7);


        assertEquals(CardSuit.RED, card.getSuit());
        assertEquals(7, card.getValue());
        assertEquals(CardType.NUMBER, card.getType());
        assertEquals("7 of red", card.toString());
    }

    @Test
    void testJesterCardCreation() {
        ICard card = CardFactory.createCard(CardSuit.YELLOW, 0);


        assertEquals(CardSuit.YELLOW, card.getSuit());
        assertEquals(0, card.getValue());
        assertEquals(CardType.JESTER, card.getType());
        assertEquals("Jester (yellow)", card.toString());
    }

    @Test
    void testWizardCardCreation() {
        ICard card = CardFactory.createCard(CardSuit.BLUE, 14);


        assertEquals(CardSuit.BLUE, card.getSuit());
        assertEquals(14, card.getValue());
        assertEquals(CardType.WIZARD, card.getType());
        assertEquals("Wizard (blue)", card.toString());
    }

    @Test
    void testEdgeCaseNumberCards() {
        ICard lowestNumber = CardFactory.createCard(CardSuit.GREEN, 1);
        ICard highestNumber = CardFactory.createCard(CardSuit.GREEN, 13);

        assertEquals(CardType.NUMBER, lowestNumber.getType());
        assertEquals(CardType.NUMBER, highestNumber.getType());
        assertEquals("1 of green", lowestNumber.toString());
        assertEquals("13 of green", highestNumber.toString());
    }

    @Test
    void testAllSuitsForNumberCard() {
        for (CardSuit suit : CardSuit.values()) {
            ICard card = CardFactory.createCard(suit, 5);

            assertEquals(suit, card.getSuit());
            assertTrue(card.toString().contains(suit.name().toLowerCase()));
        }
    }

    @Test
    void testAllSuitsForJester() {
        for (CardSuit suit : CardSuit.values()) {
            ICard card = CardFactory.createCard(suit, 0);

            assertEquals(suit, card.getSuit());
            assertTrue(card.toString().contains("Jester"));
            assertTrue(card.toString().contains(suit.name().toLowerCase()));
        }
    }

    @Test
    void testAllSuitsForWizard() {
        for (CardSuit suit : CardSuit.values()) {
            ICard card = CardFactory.createCard(suit, 14);
            assertEquals(suit, card.getSuit());
            assertTrue(card.toString().contains("Wizard"));
            assertTrue(card.toString().contains(suit.name().toLowerCase()));
        }
    }

    @Test
    void testSpecialSuitCards() {
        ICard numberSpecial = CardFactory.createCard(CardSuit.SPECIAL, 5);
        ICard jesterSpecial = CardFactory.createCard(CardSuit.SPECIAL, 0);
        ICard wizardSpecial = CardFactory.createCard(CardSuit.SPECIAL, 14);


        assertEquals("5 of special", numberSpecial.toString());
        assertEquals("Jester (special)", jesterSpecial.toString());
        assertEquals("Wizard (special)", wizardSpecial.toString());
    }

    @Test
    void hashCode_shouldBeEqualForSameCard() {
        ICard card1 = CardFactory.createCard(CardSuit.RED, 5);
        ICard card2 = CardFactory.createCard(CardSuit.RED, 5);

        assertEquals(card1.hashCode(), card2.hashCode(),
                "Two cards with same suit and value should have same hash code");
    }
}