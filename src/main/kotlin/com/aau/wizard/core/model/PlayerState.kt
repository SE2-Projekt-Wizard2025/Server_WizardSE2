package com.aau.wizard.core.model

import com.aau.wizard.core.cards.Card

data class PlayerState(
    val playerId: String,
    val name: String,
    var hand: MutableList<Card> = mutableListOf(),
    var bid: Int = 0,
    var tricksWon: Int = 0,
    var score: Int = 0
)
