package com.aau.wizard.core.game

import com.aau.wizard.core.cards.Card
import com.aau.wizard.core.cards.CardType
import com.aau.wizard.core.cards.Deck
import com.aau.wizard.core.cards.Suit
import com.aau.wizard.core.model.PlayerState
import com.aau.wizard.core.rules.TrickRules
import kotlin.math.abs

class GameManager(internal val players: List<PlayerState>) {

    //Habe die variablen auf internal gemacht, um die Tests einfacher zu machen...
    internal val deck = Deck()
    internal var trumpCard: Card? = null
    var trumpSuit: Suit? = null
    internal val playedCards = mutableListOf<Pair<PlayerState, Card>>()
    internal var currentTrickNumber = 0


    fun startRound(roundNumber: Int) {
        deck.shuffle()
        players.forEach { player ->
            player.hand = deck.draw(roundNumber).toMutableList()
            player.tricksWon = 0
            player.bid = 0
        }

        //wenn letzte Runde passiert:
        if (deck.size() < 1){
            trumpCard = null
            trumpSuit = null
        }
        else{
            trumpCard = deck.draw(1).firstOrNull()
            trumpSuit = trumpCard?.suit
        }

        currentTrickNumber = 0
        playedCards.clear()

        println("Trumpf: ${trumpSuit ?: "Kein Trumpf"}")
    }


    fun playCard(player: PlayerState, card: Card) {
        require(player.hand.contains(card)) { "Player doesn't have that card" }

        if (playedCards.isNotEmpty() && !isValidCardPlay(player, card)) {
            throw IllegalStateException("Invalid card play: $card by ${player.name}")
        }

        player.hand.remove(card)
        playedCards.add(player to card)
    }

    private fun isValidCardPlay(player: PlayerState, card: Card): Boolean {
        val leadSuit = playedCards
            .map { it.second }
            .firstOrNull { it.type == CardType.NUMBER }
            ?.suit

        if (card.type in listOf(CardType.WIZARD, CardType.JESTER)) return true

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
                difference == 0 -> 20 + (player.tricksWon * 10)
                else -> (player.tricksWon * 10) - (difference * 10)
            }
        }

        println("\n=== Finale Auswertung ===")
        players.forEach {
            println("${it.name}: ${it.bid} geboten, ${it.tricksWon} gewonnen → Punkte: ${it.score}")
        }
    }
}