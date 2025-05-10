package model;

import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.Card;
import com.aau.wizard.model.enums.CardType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testNumberCardCreation() {
        Card card = new Card(CardSuit.RED, 7);

        assertEquals(CardSuit.RED, card.getSuit());
        assertEquals(7, card.getValue());
        assertEquals(CardType.NUMBER, card.getType());
        assertEquals("7 of red", card.toString());
    }

    @Test
    void testJesterCardCreation() {
        Card card = new Card(CardSuit.YELLOW, 0);

        assertEquals(CardSuit.YELLOW, card.getSuit());
        assertEquals(0, card.getValue());
        assertEquals(CardType.JESTER, card.getType());
        assertEquals("Jester (yellow)", card.toString());
    }

    @Test
    void testWizardCardCreation() {
        Card card = new Card(CardSuit.BLUE, 14);

        assertEquals(CardSuit.BLUE, card.getSuit());
        assertEquals(14, card.getValue());
        assertEquals(CardType.WIZARD, card.getType());
        assertEquals("Wizard (blue)", card.toString());
    }

    @Test
    void testEdgeCaseNumberCards() {
        Card lowestNumber = new Card(CardSuit.GREEN, 1);
        Card highestNumber = new Card(CardSuit.GREEN, 13);

        assertEquals(CardType.NUMBER, lowestNumber.getType());
        assertEquals(CardType.NUMBER, highestNumber.getType());
        assertEquals("1 of green", lowestNumber.toString());
        assertEquals("13 of green", highestNumber.toString());
    }

    @Test
    void testAllSuitsForNumberCard() {
        for (CardSuit suit : CardSuit.values()) {
            Card card = new Card(suit, 5);
            assertEquals(suit, card.getSuit());
            assertTrue(card.toString().contains(suit.name().toLowerCase()));
        }
    }

    @Test
    void testAllSuitsForJester() {
        for (CardSuit suit : CardSuit.values()) {
            Card card = new Card(suit, 0);
            assertEquals(suit, card.getSuit());
            assertTrue(card.toString().contains("Jester"));
            assertTrue(card.toString().contains(suit.name().toLowerCase()));
        }
    }

    @Test
    void testAllSuitsForWizard() {
        for (CardSuit suit : CardSuit.values()) {
            Card card = new Card(suit, 14);
            assertEquals(suit, card.getSuit());
            assertTrue(card.toString().contains("Wizard"));
            assertTrue(card.toString().contains(suit.name().toLowerCase()));
        }
    }

    @Test
    void testSpecialSuitCards() {
        Card numberSpecial = new Card(CardSuit.SPECIAL, 5);
        Card jesterSpecial = new Card(CardSuit.SPECIAL, 0);
        Card wizardSpecial = new Card(CardSuit.SPECIAL, 14);

        assertEquals("5 of special", numberSpecial.toString());
        assertEquals("Jester (special)", jesterSpecial.toString());
        assertEquals("Wizard (special)", wizardSpecial.toString());
    }

    @Test
    void hashCode_shouldBeEqualForSameCard() {
        Card card1 = new Card(CardSuit.RED, 5);
        Card card2 = new Card(CardSuit.RED, 5);

        assertEquals(card1.hashCode(), card2.hashCode(),
                "Two cards with same suit and value should have same hash code");
    }
}