package util;

import com.aau.wizard.model.Player;
import com.aau.wizard.util.BiddingRules;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BiddingRulesTest {

    private Player createPlayer(String id, String name, int bid, int tricksWon, int currentScore) {
        Player player = new Player(id, name);
        player.setBid(bid);
        player.setTricksWon(tricksWon);
        player.setScore(currentScore);
        return player;
    }

    @Test
    void calculateScores_perfectBid() {
        Player player = createPlayer("p1", "Alice", 3, 3, 10);
        BiddingRules.calculateScores(List.of(player));

        assertEquals(60, player.getScore());
    }

    @Test
    void calculateScores_zeroBidPerfect() {
        Player player = createPlayer("p1", "Bob", 0, 0, 5);
        BiddingRules.calculateScores(List.of(player));

        assertEquals(25, player.getScore());
    }

    @Test
    void calculateScores_underbidByOne() {
        Player player = createPlayer("p1", "Charlie", 2, 3, 30);
        BiddingRules.calculateScores(List.of(player));

        assertEquals(20, player.getScore());
    }

    @Test
    void calculateScores_overbidByTwo() {
        Player player = createPlayer("p1", "Dana", 4, 2, 15);
        BiddingRules.calculateScores(List.of(player));

        assertEquals(-5, player.getScore());
    }

    @Test
    void calculateScores_multiplePlayers() {
        Player player1 = createPlayer("p1", "Alice", 3, 3, 0);  // Perfect
        Player player2 = createPlayer("p2", "Bob", 2, 0, 10);   // Under by 2
        Player player3 = createPlayer("p3", "Charlie", 1, 1, 5); // Perfect

        BiddingRules.calculateScores(List.of(player1, player2, player3));

        assertEquals(50, player1.getScore());
        assertEquals(-10, player2.getScore());
        assertEquals(35, player3.getScore());
    }

    @Test
    void calculateScores_negativeScorePossible() {
        Player player = createPlayer("p1", "Eve", 5, 0, -5);
        BiddingRules.calculateScores(List.of(player));

        assertEquals(-55, player.getScore());
    }

    @Test
    void calculateScores_emptyPlayerList() {
        BiddingRules.calculateScores(List.of());
    }

    @Test
    void calculateScores_newPlayerZeroInitialScore() {
        Player player = new Player("p1", "Newbie");
        player.setBid(2);
        player.setTricksWon(2);

        BiddingRules.calculateScores(List.of(player));

        assertEquals(40, player.getScore());
    }
}