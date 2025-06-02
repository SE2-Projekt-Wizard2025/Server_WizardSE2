package dto;

import com.aau.wizard.dto.CardDto;
import com.aau.wizard.model.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static testutil.TestDataFactory.*;
import static testutil.TestConstants.*;

class CardDtoTest {
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
        CardDto dto = createDefaultCardDto();

        assertCardDtoFields(dto);
    }

    /**
     * Verifies that CardDto.from(Card) correctly maps all enum values to strings.
     */
    /*@Test
    void testFromCard() {
        Card card = createDefaultCard();
        CardDto dto = CardDto.from(card);

        assertCardDtoFields(dto);
    }*/

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
    /*@Test
    void testSafeFromPlayerWithCard() {
        Card card = createDefaultCard();
        Player player = createDefaultPlayer();
        player.setHandCards(List.of(card));

        List<CardDto> result = CardDto.safeFromPlayer(player);

        assertNotNull(result);
        assertEquals(1, result.size());

        CardDto dto = result.get(0);
        assertCardDtoFields(dto);
    }*/

    private void assertCardDtoFields(CardDto cardDto) {
        assertEquals(TEST_CARD_COLOR, cardDto.getColor());
        assertEquals(TEST_CARD_VALUE, cardDto.getValue());
        assertEquals(TEST_CARD_TYPE, cardDto.getType());
    }

    private void assertEmptyCardList(List<CardDto> cards) {
        assertNotNull(cards);
        assertTrue(cards.isEmpty());
    }

    private CardDto createDefaultCardDtoNoArgsConstructor() {
        CardDto dto = new CardDto();
        dto.setColor(TEST_CARD_COLOR);
        dto.setValue(TEST_CARD_VALUE);
        dto.setType(TEST_CARD_TYPE);
        return dto;
    }
}
