package com.aau.wizard.core.logic

import com.aau.wizard.core.cards.Card
import com.aau.wizard.core.cards.CardType
import com.aau.wizard.core.cards.Deck
import com.aau.wizard.core.cards.Suit
import com.aau.wizard.core.model.PlayerState
import com.aau.wizard.core.rules.BiddingRules
import com.aau.wizard.core.rules.TrickRules
import kotlin.math.abs

class GameManager(private val players: List<PlayerState>) {

    private val deck = Deck()
    private var trumpCard: Card? = null
    var trumpSuit: Suit? = null // Made public for debugging
    private val playedCards = mutableListOf<Pair<PlayerState, Card>>()
    private var currentTrickNumber = 0

    fun startRound(roundNumber: Int) {
        deck.shuffle()
        players.forEach { player ->
            player.hand = deck.draw(roundNumber).toMutableList()
            player.tricksWon = 0
            player.bid = 0 // Reset bids
        }
        trumpCard = deck.draw(1).firstOrNull()
        trumpSuit = trumpCard?.suit
        currentTrickNumber = 0
        playedCards.clear()

        println("Trumpf: ${trumpSuit ?: "Kein Trumpf"}")
    }

    fun playCard(player: PlayerState, card: Card) {
        require(player.hand.contains(card)) { "Player doesn't have that card" }

        // Validate card follows suit rules (if not first card in trick)
        if (playedCards.isNotEmpty() && !isValidCardPlay(player, card)) {
            throw IllegalStateException("Invalid card play: $card by ${player.name}")
        }

        player.hand.remove(card)
        playedCards.add(player to card)
    }

    private fun isValidCardPlay(player: PlayerState, card: Card): Boolean {
        val leadSuit = playedCards
            .map { it.second }
            .firstOrNull { it.type == CardType.NUMBER } // Jester und Wizard ignorieren
            ?.suit

        // Wizard and Jester can always be played
        if (card.type in listOf(CardType.WIZARD, CardType.JESTER)) return true

        // If player has cards of lead suit, must follow suit
        if (leadSuit != null && player.hand.any { it.suit == leadSuit && it.type == CardType.NUMBER }) {
            return card.suit == leadSuit
        }

        return true
    }

    fun endTrick(): PlayerState {
        require(playedCards.isNotEmpty()) { "No cards played in this trick" }

        val winner = TrickRules.determineTrickWinner(playedCards, trumpSuit).also {
            it.tricksWon += 1
            println("Stich ${currentTrickNumber + 1} gewonnen von ${it.name} (${it.tricksWon} Stiche)")
        }

        playedCards.clear()
        currentTrickNumber++
        return winner
    }

    fun endRound() {
        players.forEach { player ->
            val difference = abs(player.tricksWon - player.bid)
            player.score += when {
                difference == 0 -> 20 + (player.tricksWon * 10) // Korrektes Gebot
                else -> (player.tricksWon * 10) - (difference * 10) // Falsches Gebot
            }
        }

        // Debug-Ausgabe
        println("\n=== Finale Auswertung ===")
        players.forEach {
            println("${it.name}: ${it.bid} geboten, ${it.tricksWon} gewonnen → Punkte: ${it.score}")
        }
    }
}