package model;

import com.aau.wizard.model.CardFactory;
import com.aau.wizard.model.ICard;
import com.aau.wizard.model.Player;
import com.aau.wizard.model.enums.CardSuit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;
    private static final String TEST_ID = "player123";
    private static final String TEST_NAME = "Test Player";

    @BeforeEach
    void setUp() {
        player = new Player(TEST_ID, TEST_NAME);
    }

    @Test
    void constructor_initializesCorrectly() {
        assertEquals(TEST_ID, player.getPlayerId());
        assertEquals(TEST_NAME, player.getName());
        assertEquals(0, player.getScore());
        assertEquals(0, player.getBid());
        assertEquals(0, player.getTricksWon());
        assertFalse(player.isReady());
        assertNotNull(player.getHandCards());
        assertTrue(player.getHandCards().isEmpty());
        assertNotNull(player.getRoundScores());
        assertTrue(player.getRoundScores().isEmpty());
    }

    @Test
    void settersAndGetters_workCorrectly() {
        // Test all setters and getters
        player.setPlayerId("newId");
        assertEquals("newId", player.getPlayerId());

        player.setName("New Name");
        assertEquals("New Name", player.getName());

        player.setScore(42);
        assertEquals(42, player.getScore());

        player.setBid(3);
        assertEquals(3, player.getBid());

        player.setTricksWon(2);
        assertEquals(2, player.getTricksWon());

        //player.setPrediction(3);
        //assertEquals(3, player.getPrediction());

        player.setReady(true);
        assertTrue(player.isReady());

        List<ICard> cards = List.of(CardFactory.createCard(CardSuit.RED, 5));
        player.setHandCards(cards);
        assertEquals(cards, player.getHandCards());
    }

    @Test
    void playersWithSameValues_haveEqualFields() {
        Player samePlayer = new Player(TEST_ID, TEST_NAME);
        assertEquals(player.getPlayerId(), samePlayer.getPlayerId());
        assertEquals(player.getName(), samePlayer.getName());
    }


    @Test
    void equals_returnsFalseForDifferentValues() {
        Player differentId = new Player("differentId", TEST_NAME);
        assertNotEquals(player, differentId);

        Player differentName = new Player(TEST_ID, "Different Name");
        assertNotEquals(player, differentName);

        Player differentPlayer = new Player(TEST_ID, TEST_NAME);
        differentPlayer.setScore(10);
        assertNotEquals(player, differentPlayer);
    }

    @Test
    void equals_handlesNullAndWrongType() {
        assertNotEquals(null, player);
        assertNotEquals("Not a player", player);
    }

    @Test
    void hashCode_consistentForSameObject() {
        int initialHash = player.hashCode();
        assertEquals(initialHash, player.hashCode());
    }

    /*@Test
    void hashCode_changesWhenFieldsChange() {
        int initialHash = player.hashCode();
        player.setScore(10);
        assertNotEquals(initialHash, player.hashCode());
    }

    @Test
    void hashCode_equalForEqualObjects() {
        Player samePlayer = new Player(TEST_ID, TEST_NAME);
        assertEquals(player.hashCode(), samePlayer.hashCode());
    } */

    @Test
    void toString_containsClassName() {
        String result = player.toString();
        assertTrue(result.contains("Player"));
    }


    @Test
    void handCards_modification() {
        List<ICard> cards = new ArrayList<>();
        cards.add(CardFactory.createCard(CardSuit.RED, 5));
        player.setHandCards(cards);

        player.getHandCards().add(CardFactory.createCard(CardSuit.BLUE, 7));
        assertEquals(2, player.getHandCards().size());
    }

    @Test
    void playersWithSameCards_haveEqualHandCards() {
        Player playerWithCards = new Player(TEST_ID, TEST_NAME);
        playerWithCards.setHandCards(List.of(CardFactory.createCard(CardSuit.RED, 5)));

        Player sameCards = new Player(TEST_ID, TEST_NAME);
        sameCards.setHandCards(List.of(CardFactory.createCard(CardSuit.RED, 5)));

        Player differentCards = new Player(TEST_ID, TEST_NAME);
        differentCards.setHandCards(List.of(CardFactory.createCard(CardSuit.BLUE, 7)));

        assertEquals(playerWithCards.getHandCards(), sameCards.getHandCards());
        assertNotEquals(playerWithCards.getHandCards(), differentCards.getHandCards());
    }
    @Test
    void addRoundScore_addsScoreToRoundScoresList() {
        player.addRoundScore(10);
        player.addRoundScore(-5);

        List<Integer> expectedScores = List.of(10, -5);
        assertEquals(expectedScores, player.getRoundScores(), "Round scores sollten korrekt hinzugefügt werden.");
        assertEquals(2, player.getRoundScores().size(), "Die Größe der Rundenpunkte-Liste sollte korrekt sein.");
    }

    @Test
    void getRoundScores_returnsCorrectList() {

        assertNotNull(player.getRoundScores());
        assertTrue(player.getRoundScores().isEmpty());

        List<Integer> scores = new ArrayList<>();
        scores.add(20);

        player.addRoundScore(20);
        assertEquals(scores, player.getRoundScores());
    }

}