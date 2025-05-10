package com.aau.wizard.util;

import com.aau.wizard.model.Player;

import java.util.List;

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
