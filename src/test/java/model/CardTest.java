package model;

import com.aau.wizard.model.Card;
import com.aau.wizard.model.enums.CardColor;
import com.aau.wizard.model.enums.CardType;
import com.aau.wizard.model.enums.CardValue;
import static com.aau.wizard.testutil.TestDataFactory.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    private Card testCard;

    @BeforeEach
    void setup() {
        testCard = createDefaultCard();
    }

    @Test
    void testConstructorAndGetters() {
        assertCardValues(testCard);
    }

    @Test
    void testSetColor() {
        testCard.setColor(CardColor.GREEN);

        assertEquals(CardColor.GREEN, testCard.getColor());
    }

    @Test
    void testSetValue() {
        testCard.setValue(CardValue.EIGHT);

        assertEquals(CardValue.EIGHT, testCard.getValue());
    }

    @Test
    void testSetType() {
        testCard.setType(CardType.WIZARD);

        assertEquals(CardType.WIZARD, testCard.getType());
    }

    private void assertCardValues(Card card) {
        assertEquals(CardColor.RED, card.getColor());
        assertEquals(CardValue.ONE, card.getValue());
        assertEquals(CardType.NORMAL, card.getType());
    }
}
