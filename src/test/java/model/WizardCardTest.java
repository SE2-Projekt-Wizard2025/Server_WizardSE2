package model;

import static org.junit.jupiter.api.Assertions.*;

import com.aau.wizard.model.JesterCard;
import com.aau.wizard.model.NumberCard;
import com.aau.wizard.model.WizardCard;
import com.aau.wizard.model.enums.CardSuit;
import org.junit.jupiter.api.Test;

public class WizardCardTest {

    @Test
    void equals_SameInstance_ReturnsTrue() {
        WizardCard card = new WizardCard(CardSuit.SPECIAL);
        assertTrue(card.equals(card)); // Reflexivität
    }

    @Test
    void equals_Null_ReturnsFalse() {
        WizardCard card = new WizardCard(CardSuit.SPECIAL);
        assertFalse(card.equals(null)); // Nicht-null-Prüfung
    }

    @Test
    void equals_DifferentClass_ReturnsFalse() {
        WizardCard wizard = new WizardCard(CardSuit.SPECIAL);
        JesterCard jester = new JesterCard(CardSuit.SPECIAL);
        assertFalse(wizard.equals(jester)); // Typenprüfung
    }

    @Test
    void equals_SameClass_ReturnsTrue() {
        WizardCard wizard1 = new WizardCard(CardSuit.SPECIAL);
        WizardCard wizard2 = new WizardCard(CardSuit.SPECIAL);
        assertTrue(wizard1.equals(wizard2)); // Gleichheit bei gleicher Klasse
    }

    @Test
    void hashCode_ForEqualObjects_ShouldBeEqual() {
        WizardCard wizard1 = new WizardCard(CardSuit.SPECIAL);
        WizardCard wizard2 = new WizardCard(CardSuit.SPECIAL);
        assertEquals(wizard1.hashCode(), wizard2.hashCode()); // Konsistenz mit equals()
    }

    @Test
    void hashCode_ForSameObject_ShouldBeConsistent() {
        WizardCard wizard = new WizardCard(CardSuit.SPECIAL);
        int initialHash = wizard.hashCode();
        assertEquals(initialHash, wizard.hashCode()); // Konsistenz über Aufrufe
    }

    @Test
    void isWizard_WithWizardCard_ReturnsTrue() {
        WizardCard wizard = new WizardCard(CardSuit.SPECIAL);
        assertTrue(WizardCard.isWizard(wizard));
    }

    @Test
    void isWizard_WithNonWizardCard_ReturnsFalse() {
        JesterCard jester = new JesterCard(CardSuit.SPECIAL);
        NumberCard numberCard = new NumberCard(CardSuit.RED, 5);
        assertFalse(WizardCard.isWizard(jester));
        assertFalse(WizardCard.isWizard(numberCard));
    }

    @Test
    void isWizard_WithNull_ReturnsFalse() {
        assertFalse(WizardCard.isWizard(null));
    }
}