package com.aau.wizard.core.cards

enum class Suit {
    RED, YELLOW, BLUE, GREEN, SPECIAL
}

enum class CardType {
    NUMBER, WIZARD, JESTER
}

data class Card(
    val suit: Suit,
    val value: Int, // 1- 13 für Zahlenkarten, 0 für den Jester, 14 fürn Wizard
    val type: CardType = when {
        value == 0 -> CardType.JESTER
        value == 14 -> CardType.WIZARD
        else -> CardType.NUMBER
    }
) {
    override fun toString(): String = when (type) {
        CardType.WIZARD -> "Wizard (${suit.name.lowercase()})"
        CardType.JESTER -> "Jester (${suit.name.lowercase()})"
        else -> "$value of ${suit.name.lowercase()}"
    }
}