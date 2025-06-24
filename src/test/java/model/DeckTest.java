package model;

import static org.junit.jupiter.api.Assertions.*;

import com.aau.wizard.model.Deck;
import com.aau.wizard.model.ICard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

class DeckTest {
    private Deck deck;

    @BeforeEach
    void setUp() {
        deck = new Deck();
        deck.shuffle();
    }

    @Test
    void draw_ValidAmount_ShouldReturnCorrectNumberOfCards() {
        int initialSize = deck.size();
        int drawAmount = 5;

        List<ICard> drawnCards = deck.draw(drawAmount);

        assertEquals(drawAmount, drawnCards.size());
        assertEquals(initialSize - drawAmount, deck.size());
    }


    @Test
    void draw_SingleCard_ShouldRemoveCardFromDeck() {
        int initialSize = deck.size();
        List<ICard> drawnCard = deck.draw(1);

        assertEquals(1, drawnCard.size());
        assertEquals(initialSize - 1, deck.size());
        assertNotEquals(deck.size(), initialSize);
    }

    @Test
    void draw_EntireDeck_ShouldEmptyDeck() {
        int deckSize = deck.size();
        List<ICard> allCards = deck.draw(deckSize);

        assertEquals(deckSize, allCards.size());
        assertEquals(0, deck.size());
        assertEquals(0, deck.size());
    }

    @Test
    void draw_AmountZero_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> deck.draw(0));
    }

    @Test
    void draw_AmountNegative_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> deck.draw(-1));
    }

    @Test
    void draw_AmountExceedsDeckSize_ShouldThrowException() {
        int deckSize = deck.size();
        assertThrows(IllegalArgumentException.class, () -> deck.draw(deckSize + 1));
    }
}