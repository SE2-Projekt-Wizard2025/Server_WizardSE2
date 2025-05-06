package com.aau.wizard.core.rules

import com.aau.wizard.core.cards.*
import com.aau.wizard.core.model.PlayerState
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows

class TrickRulesTest {
    private val alice = PlayerState("p1", "Alice")
    private val bob = PlayerState("p2", "Bob")
    private val charlie = PlayerState("p3", "Charlie")

    @Nested
    inner class BasicTrickTests {
        @Test
        fun `throws when no cards played`() {
            assertThrows<IllegalArgumentException> {
                TrickRules.determineTrickWinner(emptyList(), trumpSuit = null)
            }
        }

        @Test
        fun `highest card of lead suit wins with no trump`() {
            val playedCards = listOf(
                alice to Card(Suit.RED, 5),
                bob to Card(Suit.RED, 10),
                charlie to Card(Suit.RED, 7)
            )

            assertEquals(bob, TrickRules.determineTrickWinner(playedCards, trumpSuit = null))
        }

        @Test
        fun `trump card beats lead suit`() {
            val playedCards = listOf(
                alice to Card(Suit.RED, 10), // Highest lead suit
                bob to Card(Suit.GREEN, 5),  // Off-suit
                charlie to Card(Suit.BLUE, 7) // Trump (assuming BLUE is trump)
            )

            assertEquals(charlie, TrickRules.determineTrickWinner(playedCards, trumpSuit = Suit.BLUE))
        }
    }

    @Nested
    inner class SpecialCardTests {
        @Test
        fun `wizard always wins`() {
            val playedCards = listOf(
                alice to Card(Suit.RED, 13), // Highest normal card
                bob to Card(Suit.SPECIAL, 14), // Wizard
                charlie to Card(Suit.GREEN, 1)
            )

            assertEquals(bob, TrickRules.determineTrickWinner(playedCards, trumpSuit = Suit.RED))
        }

        @Test
        fun `jester always loses unless all jesters`() {
            val playedCards = listOf(
                alice to Card(Suit.SPECIAL, 0), // Jester
                bob to Card(Suit.RED, 2),       // Low but beats jester
                charlie to Card(Suit.GREEN, 1)  // Lowest non-jester
            )

            assertEquals(bob, TrickRules.determineTrickWinner(playedCards, trumpSuit = null))
        }

        @Test
        fun `all jesters returns first player`() {
            val playedCards = listOf(
                alice to Card(Suit.SPECIAL, 0),
                bob to Card(Suit.SPECIAL, 0),
                charlie to Card(Suit.SPECIAL, 0)
            )

            assertEquals(alice, TrickRules.determineTrickWinner(playedCards, trumpSuit = Suit.RED))
        }
    }

    @Nested
    inner class LeadSuitTests {
        @Test
        fun `first non-jester determines lead suit`() {
            val playedCards = listOf(
                alice to Card(Suit.SPECIAL, 0), // Jester
                bob to Card(Suit.YELLOW, 5),    // First non-jester
                charlie to Card(Suit.YELLOW, 7)
            )

            assertEquals(charlie, TrickRules.determineTrickWinner(playedCards, trumpSuit = null))
        }

        @Test
        fun `wizard as first card means no lead suit`() {
            val playedCards = listOf(
                alice to Card(Suit.SPECIAL, 14), // Wizard
                bob to Card(Suit.RED, 10),       // Would be lead suit if not for wizard
                charlie to Card(Suit.GREEN, 7)
            )

            // Highest non-wizard wins (since no lead suit established)
            assertEquals(bob, TrickRules.determineTrickWinner(playedCards, trumpSuit = null))
        }
    }

    @Nested
    inner class TrumpEdgeCases {
        @Test
        fun `higher trump beats lower trump`() {
            val playedCards = listOf(
                alice to Card(Suit.BLUE, 5),  // Trump
                bob to Card(Suit.BLUE, 10),   // Higher trump
                charlie to Card(Suit.RED, 13) // High non-trump
            )

            assertEquals(bob, TrickRules.determineTrickWinner(playedCards, trumpSuit = Suit.BLUE))
        }

        @Test
        fun `trump beats lead suit even when lower value`() {
            val playedCards = listOf(
                alice to Card(Suit.RED, 13), // Highest lead suit
                bob to Card(Suit.BLUE, 1),   // Lowest trump
                charlie to Card(Suit.RED, 10)
            )

            assertEquals(bob, TrickRules.determineTrickWinner(playedCards, trumpSuit = Suit.BLUE))
        }
    }
}