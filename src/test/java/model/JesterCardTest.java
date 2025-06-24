package model;

import static org.junit.jupiter.api.Assertions.*;

import com.aau.wizard.model.JesterCard;
import com.aau.wizard.model.WizardCard;
import org.junit.jupiter.api.Test;
import com.aau.wizard.model.enums.CardSuit;

class JesterCardTest {

    @Test
    void equals_ShouldBeReflexive() {
        JesterCard card = new JesterCard(CardSuit.SPECIAL);
        assertTrue(card.equals(card));

    }

    @Test
    void equals_WithNull_ShouldReturnFalse() {
        JesterCard card = new JesterCard(CardSuit.SPECIAL);
        assertFalse(card.equals(null));
    }

    @Test
    void equals_WithDifferentClass_ShouldReturnFalse() {
        JesterCard card = new JesterCard(CardSuit.SPECIAL);
        WizardCard wizard = new WizardCard(CardSuit.SPECIAL);
        assertFalse(card.equals(wizard));
    }

    @Test
    void equals_WithSameClass_ShouldReturnTrue() {
        JesterCard card1 = new JesterCard(CardSuit.SPECIAL);
        JesterCard card2 = new JesterCard(CardSuit.SPECIAL);
        assertTrue(card1.equals(card2));
    }


    @Test
    void hashCode_ForEqualObjects_ShouldBeEqual() {
        JesterCard card1 = new JesterCard(CardSuit.SPECIAL);
        JesterCard card2 = new JesterCard(CardSuit.SPECIAL);
        assertEquals(card1.hashCode(), card2.hashCode());
    }


    @Test
    void hashCode_ForSameObject_ShouldBeConsistent() {
        JesterCard card = new JesterCard(CardSuit.SPECIAL);
        int initialHash = card.hashCode();
        assertEquals(initialHash, card.hashCode());
    }
}

