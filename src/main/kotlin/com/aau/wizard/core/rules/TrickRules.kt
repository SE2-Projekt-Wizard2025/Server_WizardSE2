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
                    card.suit == trumpSuit -> 1000 + card.value // Trumpf hat höchste Priorität
                    card.suit == leadSuit -> 100 + card.value // Lead-Farbe kommt danach
                    else -> 0 // Andere Farben zählen nicht
                }
            }
            player to score
        }

        return ranked.maxByOrNull { it.second }?.first ?: error("No winner determined")
    }
}
