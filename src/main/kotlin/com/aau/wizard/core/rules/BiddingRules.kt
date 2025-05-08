/*package com.aau.wizard.core.rules

import com.aau.wizard.core.model.PlayerState

object BiddingRules {
    fun calculateScores(players: List<PlayerState>) {
        for (player in players) {
            player.score += if (player.bid == player.tricksWon) {
                20 + (10 * player.bid)
            } else {
                -10 * kotlin.math.abs(player.bid - player.tricksWon)
            }
        }
    }
}
*/