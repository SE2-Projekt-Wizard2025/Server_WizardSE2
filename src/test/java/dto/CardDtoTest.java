package dto;

import com.aau.wizard.dto.CardDto;
import com.aau.wizard.model.Card;
import com.aau.wizard.model.Player;
import com.aau.wizard.model.enums.CardColor;
import com.aau.wizard.model.enums.CardType;
import com.aau.wizard.model.enums.CardValue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardDtoTest {

    private static final String COLOR = "RED";
    private static final String VALUE = "SEVEN";
    private static final String TYPE = "NORMAL";

    /**
     * Verifies that the constructor and getters/setters of CardDto work as expected.
     */
    @Test
    void testNoArgsConstructorAndSetters() {
        CardDto dto = createDefaultCardDtoNoArgsConstructor();

        assertCardDtoFields(dto);
    }

    /**
     * Verifies that the all-args constructor initializes all fields correctly.
     */
    @Test
    void testAllArgsConstructor() {
        CardDto dto = createDefaultCardDtoAllArgsConstructor();

        assertCardDtoFields(dto);
    }

    /**
     * Verifies that CardDto.from(Card) correctly maps all enum values to strings.
     */
    @Test
    void testFromCard() {
        Card card = createDefaultCard();
        CardDto dto = CardDto.from(card);

        assertCardDtoFields(dto);
    }

    /**
     * Verifies that safeFromPlayer returns an empty list when player is null.
     */
    @Test
    void testSafeFromPlayerWithNullPlayer() {
        List<CardDto> result = CardDto.safeFromPlayer(null);

        assertEmptyCardList(result);
    }

    /**
     * Verifies that safeFromPlayer returns an empty list when the player has no cards.
     */
    @Test
    void testSafeFromPlayerWithEmptyHand() {
        Player player = createDefaultPlayer();
        player.setHandCards(List.of()); // explicitly empty

        List<CardDto> result = CardDto.safeFromPlayer(player);

        assertEmptyCardList(result);
    }

    /**
     * Verifies that safeFromPlayer correctly maps a player's hand cards.
     */
    @Test
    void testSafeFromPlayerWithCard() {
        Card card = createDefaultCard();
        Player player = createDefaultPlayer();
        player.setHandCards(List.of(card));

        List<CardDto> result = CardDto.safeFromPlayer(player);

        assertNotNull(result);
        assertEquals(1, result.size());

        CardDto dto = result.get(0);
        assertCardDtoFields(dto);
    }

    private void assertCardDtoFields(CardDto cardDto) {
        assertEquals(COLOR, cardDto.getColor());
        assertEquals(VALUE, cardDto.getValue());
        assertEquals(TYPE, cardDto.getType());
    }

    private void assertEmptyCardList(List<CardDto> cards) {
        assertNotNull(cards);
        assertTrue(cards.isEmpty());
    }

    private Player createDefaultPlayer() {
        return new Player("id", "name");
    }

    private Card createDefaultCard() {
        return new Card(CardColor.RED, CardValue.SEVEN, CardType.NORMAL);
    }

    private CardDto createDefaultCardDtoNoArgsConstructor() {
        CardDto dto = new CardDto();
        dto.setColor(COLOR);
        dto.setValue(VALUE);
        dto.setType(TYPE);
        return dto;
    }

    private CardDto createDefaultCardDtoAllArgsConstructor() {
        return new CardDto(COLOR, VALUE, TYPE);
    }
}
