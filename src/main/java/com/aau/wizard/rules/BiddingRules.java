package com.aau.wizard.rules;

import com.aau.wizard.model.Player;

import java.util.List;

public class BiddingRules {

    public static void calculateScores(List<Player> players) {
        for (Player player : players) {
            int difference = Math.abs(player.getPrediction() - player.getTricksWon());
            if (difference == 0) {
                player.setScore(player.getScore() + 20 + (10 * player.getTricksWon()));
            } else {
                player.setScore(player.getScore() - 10 * difference);
            }
        }
    }
}
