package com.aau.wizard.util;

import com.aau.wizard.model.PlayerState;

import java.util.List;

public class BiddingRules {

    public static void calculateScores(List<PlayerState> players) {
        for (PlayerState player : players) {
            int difference = Math.abs(player.getBid() - player.getTricksWon());
            if (difference == 0) {
                player.setScore(player.getScore() + 20 + (10 * player.getBid()));
            } else {
                player.setScore(player.getScore() - 10 * difference);
            }
        }
    }
}
