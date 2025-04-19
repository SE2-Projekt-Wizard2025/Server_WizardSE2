package model;

import com.aau.wizard.model.Card;
import com.aau.wizard.model.Player;
import static com.aau.wizard.testutil.TestConstants.*;
import static com.aau.wizard.testutil.TestDataFactory.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player testPlayer;

    @BeforeEach
    void setup() {
        testPlayer = createDefaultPlayer();
    }

    @Test
    void testConstructorAndGetters() {
        assertPlayerValues(testPlayer);
    }

    @Test
    void testSetPlayerIdAndName() {
        testPlayer.setPlayerId("id456");
        testPlayer.setName("Bob");

        assertEquals("id456", testPlayer.getPlayerId());
        assertEquals("Bob", testPlayer.getName());
    }

    @Test
    void testSetAndGetScore() {
        testPlayer.setScore(42);
        assertEquals(42, testPlayer.getScore());
    }

    @Test
    void testSetAndGetReady() {
        testPlayer.setReady(true);
        assertTrue(testPlayer.isReady());

        testPlayer.setReady(false);
        assertFalse(testPlayer.isReady());
    }

    @Test
    void testSetAndGetHandCards() {
        List<Card> cards = createDefaultListOfCard();
        testPlayer.setHandCards(cards);

        assertEquals(2, testPlayer.getHandCards().size());
        assertEquals(cards.get(0), testPlayer.getHandCards().get(0));
    }

    private void assertPlayerValues(Player player) {
        assertEquals(TEST_PLAYER_ID, player.getPlayerId());
        assertEquals(TEST_PLAYER_NAME, player.getName());
        assertEquals(0, player.getScore());
        assertFalse(player.isReady());
        assertNotNull(player.getHandCards());
        assertTrue(player.getHandCards().isEmpty());
    }
}
