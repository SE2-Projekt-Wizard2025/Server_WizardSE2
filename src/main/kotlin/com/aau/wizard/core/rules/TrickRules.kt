package com.aau.wizard.core.rules

import com.aau.wizard.core.cards.*
import com.aau.wizard.core.model.PlayerState

object TrickRules {
    fun determineTrickWinner(playedCards: List<Pair<PlayerState, Card>>, trumpSuit: Suit?): PlayerState {
        require(playedCards.isNotEmpty()) { "No cards played" }

        val leadSuit = playedCards.first().let { (_, card) ->
            when (card.type) {
                CardType.JESTER -> playedCards.firstOrNull { it.second.type != CardType.JESTER }?.second?.suit
                CardType.WIZARD -> null
                else -> card.suit
            }
        }

        val ranked = playedCards.map { (player, card) ->
            val score = when (card.type) {
                CardType.WIZARD -> Int.MAX_VALUE // Wizard gewinnt immer
                CardType.JESTER -> Int.MIN_VALUE // Jester verliert immer
                else -> when {
                    card.suit == trumpSuit -> 1000 + card.value
                    card.suit == leadSuit -> 100 + card.value
                    else -> 0
                }
            }
            player to score
        }

        return ranked.maxByOrNull { it.second }?.first ?: error("No winner determined")
    }

    fun isValidPlay(player: PlayerState, card: Card, currentTrick: List<Pair<PlayerState, Card>>): Boolean {
        if (card.type == CardType.WIZARD || card.type == CardType.JESTER) return true //die kann man immer spielen
        if (currentTrick.isEmpty()) return true //wenn man Erster ist

        val leadSuit = currentTrick.first().second.suit
        val hasLeadSuit = player.hand.any { it.suit == leadSuit }
        return !hasLeadSuit || card.suit == leadSuit //hat die Karte nicht, oder hat passende Farbe gewählt
    }
}
