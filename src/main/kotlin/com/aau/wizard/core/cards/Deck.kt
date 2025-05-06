package com.aau.wizard.core.cards

class Deck {
    private val cards: MutableList<Card> = mutableListOf()

    init {
        initializeDeck()
    }

    private fun initializeDeck() {
        // Standard Karten (1-13 in jeder Farbe)
        Suit.values().filter { it != Suit.SPECIAL }.forEach { suit ->
            (1..13).forEach { value ->
                cards.add(Card(suit, value))
            }
        }

        //Spezialkarten (4 Zauberer + 4 Narren)
        repeat(4) {
            cards.add(Card(Suit.SPECIAL, 14)) // Wizard
            cards.add(Card(Suit.SPECIAL, 0))  // Jester
        }
    }


    fun shuffle() {
        cards.shuffle()
    }

    fun draw(amount: Int): List<Card> {
        require(amount in 1..cards.size) { "Cannot draw $amount cards from deck" }
        return List(amount) { cards.removeAt(0) }
    }

    fun size(): Int = cards.size
}