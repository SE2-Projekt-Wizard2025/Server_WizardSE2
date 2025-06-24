package model;

import static org.junit.jupiter.api.Assertions.*;

import com.aau.wizard.model.ICard;
import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.CardType;
import org.junit.jupiter.api.Test;

class ICardTest {


    @Test
    public void testFromString_Wizard() {
        ICard card = ICard.fromString("WIZARD");
        assertEquals(CardSuit.SPECIAL, card.getSuit());
        assertEquals(CardType.WIZARD, card.getType());
    }

    @Test
    public void testFromString_Jester() {
        ICard card = ICard.fromString("JESTER");
        assertEquals(CardSuit.SPECIAL, card.getSuit());
        assertEquals(CardType.JESTER, card.getType());
    }

    @Test
    public void testFromString_NumberCard() {
        ICard card = ICard.fromString("RED_10");
        assertEquals(CardSuit.RED, card.getSuit());
        assertEquals(10, card.getValue());
        assertEquals(CardType.NUMBER, card.getType());
    }

    @Test
    public void testFromString_EmptyString_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ICard.fromString(""));
    }

    @Test
    public void testFromString_InvalidFormat_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ICard.fromString("RED_10_EXTRA"));
    }

    @Test
    public void testFromString_InvalidSuit_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ICard.fromString("INVALID_5"));
    }

    @Test
    public void testFromString_InvalidValue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ICard.fromString("RED_INVALID"));
    }

    @Test
    public void testFromString_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ICard.fromString(null));
    }
}