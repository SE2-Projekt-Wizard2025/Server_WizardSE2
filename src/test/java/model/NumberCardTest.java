package model;

import static org.junit.jupiter.api.Assertions.*;

import com.aau.wizard.model.NumberCard;
import com.aau.wizard.model.WizardCard;
import com.aau.wizard.model.enums.CardSuit;
import org.junit.jupiter.api.Test;

public class NumberCardTest {
    @Test
    void constructor_InvalidValueBelowRange_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new NumberCard(CardSuit.RED, 0));
    }

    @Test
    void constructor_InvalidValueAboveRange_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new NumberCard(CardSuit.BLUE, 14));
    }

    @Test
    void constructor_ValidValue_CreatesInstance() {
        NumberCard card = new NumberCard(CardSuit.GREEN, 5);
        assertEquals(5, card.getValue());
        assertEquals(CardSuit.GREEN, card.getSuit());
    }


    @Test
    void equals_SameInstance_ReturnsTrue() {
        NumberCard card = new NumberCard(CardSuit.YELLOW, 7);
        assertTrue(card.equals(card));
    }

    @Test
    void equals_Null_ReturnsFalse() {
        NumberCard card = new NumberCard(CardSuit.RED, 3);
        assertFalse(card.equals(null));
    }

    @Test
    void equals_DifferentClass_ReturnsFalse() {
        NumberCard card = new NumberCard(CardSuit.BLUE, 10);
        WizardCard wizard = new WizardCard(CardSuit.SPECIAL);
        assertFalse(card.equals(wizard));
    }

    @Test
    void equals_SameSuitAndValue_ReturnsTrue() {
        NumberCard card1 = new NumberCard(CardSuit.GREEN, 2);
        NumberCard card2 = new NumberCard(CardSuit.GREEN, 2);
        assertTrue(card1.equals(card2));
    }

    @Test
    void equals_DifferentSuit_ReturnsFalse() {
        NumberCard card1 = new NumberCard(CardSuit.YELLOW, 5);
        NumberCard card2 = new NumberCard(CardSuit.RED, 5);
        assertFalse(card1.equals(card2));
    }

    @Test
    void equals_DifferentValue_ReturnsFalse() {
        NumberCard card1 = new NumberCard(CardSuit.BLUE, 8);
        NumberCard card2 = new NumberCard(CardSuit.BLUE, 9);
        assertFalse(card1.equals(card2));
    }


    @Test
    void hashCode_ConsistentWithEquals() {
        NumberCard card1 = new NumberCard(CardSuit.RED, 12);
        NumberCard card2 = new NumberCard(CardSuit.RED, 12);
        assertEquals(card1.hashCode(), card2.hashCode());
    }

    @Test
    void hashCode_DifferentSuitOrValue_ReturnsDifferentHash() {
        NumberCard card1 = new NumberCard(CardSuit.GREEN, 1);
        NumberCard card2 = new NumberCard(CardSuit.YELLOW, 1);
        assertNotEquals(card1.hashCode(), card2.hashCode());
        NumberCard card3 = new NumberCard(CardSuit.BLUE, 13);
        NumberCard card4 = new NumberCard(CardSuit.BLUE, 12);
        assertNotEquals(card3.hashCode(), card4.hashCode());
    }

}

