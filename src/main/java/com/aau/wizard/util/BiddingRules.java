package com.aau.wizard.util;

import com.aau.wizard.model.Player;

import java.util.List;

// fixme avoid static methods in OO languages for extensibility/polymorphism
// fixme this is not a util but a game rule -> move to some game logic package
public class BiddingRules {

    public static void calculateScores(List<Player> players) {
        for (Player player : players) {
            int difference = Math.abs(player.getBid() - player.getTricksWon());
            if (difference == 0) {
                player.setScore(player.getScore() + 20 + (10 * player.getBid()));
            } else {
                player.setScore(player.getScore() - 10 * difference);
            }
        }
    }
}
