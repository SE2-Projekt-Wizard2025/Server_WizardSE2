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
                alice to Card(Suit.RED, 10),
                bob to Card(Suit.GREEN, 5),
                charlie to Card(Suit.BLUE, 7)
            )

            assertEquals(charlie, TrickRules.determineTrickWinner(playedCards, trumpSuit = Suit.BLUE))
        }
    }

    @Nested
    inner class SpecialCardTests {
        @Test
        fun `wizard always wins`() {
            val playedCards = listOf(
                alice to Card(Suit.RED, 13),
                bob to Card(Suit.SPECIAL, 14),
                charlie to Card(Suit.GREEN, 1)
            )

            assertEquals(bob, TrickRules.determineTrickWinner(playedCards, trumpSuit = Suit.RED))
        }

        @Test
        fun `jester always loses unless all jesters`() {
            val playedCards = listOf(
                alice to Card(Suit.SPECIAL, 0),
                bob to Card(Suit.RED, 2),
                charlie to Card(Suit.GREEN, 1)
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
                alice to Card(Suit.SPECIAL, 0),
                bob to Card(Suit.YELLOW, 5),
                charlie to Card(Suit.YELLOW, 7)
            )

            assertEquals(charlie, TrickRules.determineTrickWinner(playedCards, trumpSuit = null))
        }

        @Test
        fun `wizard as first card means no lead suit`() {
            val playedCards = listOf(
                alice to Card(Suit.SPECIAL, 14),
                bob to Card(Suit.RED, 10),
                charlie to Card(Suit.GREEN, 7)
            )

            assertEquals(alice, TrickRules.determineTrickWinner(playedCards, trumpSuit = null))
        }

        @Test
        fun `player must follow lead suit if possible`() {
            val player = PlayerState(
                playerId = "player1",
                name = "Alice",
                hand = mutableListOf(
                    Card(Suit.RED, 10),
                    Card(Suit.GREEN, 7)
                )
            )

            val currentTrick = listOf(
                Pair(PlayerState("player2", "Bob", mutableListOf()), Card(Suit.RED, 5))
            )

            assertFalse(
                TrickRules.isValidPlay(player, Card(Suit.GREEN, 7), currentTrick),
                "Player should not be allowed to play GREEN when holding RED"
            )

            assertTrue(
                TrickRules.isValidPlay(player, Card(Suit.RED, 10), currentTrick),
                "Player should be allowed to play RED (lead suit)"
            )
        }
    }

    @Nested
    inner class TrumpEdgeCases {
        @Test
        fun `higher trump beats lower trump`() {
            val playedCards = listOf(
                alice to Card(Suit.BLUE, 5),
                bob to Card(Suit.BLUE, 10),
                charlie to Card(Suit.RED, 13)
            )

            assertEquals(bob, TrickRules.determineTrickWinner(playedCards, trumpSuit = Suit.BLUE))
        }

        @Test
        fun `trump beats lead suit even when lower value`() {
            val playedCards = listOf(
                alice to Card(Suit.RED, 13),
                bob to Card(Suit.BLUE, 1),
                charlie to Card(Suit.RED, 10)
            )

            assertEquals(bob, TrickRules.determineTrickWinner(playedCards, trumpSuit = Suit.BLUE))
        }
    }
}