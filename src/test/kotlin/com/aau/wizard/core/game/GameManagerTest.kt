package com.aau.wizard.core.game

import com.aau.wizard.core.cards.*
import com.aau.wizard.core.model.PlayerState
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class GameManagerTest {

    private lateinit var alice: PlayerState
    private lateinit var bob: PlayerState
    private lateinit var manager: GameManager
    private lateinit var originalOut: PrintStream

    @BeforeEach
    fun setup() {
        alice = PlayerState(playerId = "p1", name = "Alice")
        bob = PlayerState(playerId = "p2", name = "Bob")
        manager = GameManager(listOf(alice, bob))
        originalOut = System.out
    }

    @Test
    fun `player must follow suit if able`() {

        alice.hand = mutableListOf(Card(Suit.RED, 5))
        bob.hand = mutableListOf(Card(Suit.RED, 10), Card(Suit.GREEN, 9))

        manager.playCard(alice, alice.hand[0])
        val ex = assertThrows(IllegalStateException::class.java) {
            manager.playCard(bob, Card(Suit.GREEN, 9)) // falsche Farbe, obwohl RED vorhanden
        }
        assertTrue(ex.message!!.contains("Invalid card play"))
    }

    @Test
    fun `wizard beats normal cards`() {
        alice.hand = mutableListOf(Card(Suit.SPECIAL, 14)) // Wizard
        bob.hand = mutableListOf(Card(Suit.RED, 13))        // normale hohe Karte

        manager.playCard(alice, alice.hand[0])
        manager.playCard(bob, bob.hand[0])
        val winner = manager.endTrick()

        assertEquals(alice, winner)
        assertEquals(1, alice.tricksWon)
        assertEquals(0, bob.tricksWon)
    }

    @Test
    fun `endRound calculates correct points for correct bid`() {
        alice.tricksWon = 2
        alice.bid = 2
        alice.score = 0

        bob.tricksWon = 1
        bob.bid = 0
        bob.score = 0

        manager.endRound()

        assertEquals(20 + (2 * 10), alice.score)  // 40 Punkte
        assertEquals((1 * 10) - (1 * 10), bob.score)  // 0 Punkte
    }

    @Test
    fun `endRound calculates penalty for wrong bid`() {
        alice.tricksWon = 1
        alice.bid = 3
        alice.score = 0

        manager.endRound()

        assertEquals((1 * 10) - (2 * 10), alice.score)  // -10 Punkte
    }


    @Nested
    inner class StartRoundTests {
        @Test
        fun `startRound shuffles deck and deals cards`() {
            manager.startRound(3)

            assertEquals(3, alice.hand.size)
            assertEquals(3, bob.hand.size)
            assertEquals(0, alice.tricksWon)
            assertEquals(0, bob.tricksWon)
            assertEquals(0, alice.bid)
            assertEquals(0, bob.bid)
        }

        @Test
        fun `startRound sets trump card when available`() {

            val originalDeckSize = manager.deck.size()

            manager.startRound(1)

            assertNotNull(manager.trumpCard)
            assertNotNull(manager.trumpSuit)
            assertEquals(originalDeckSize - (2 * 1 + 1), manager.deck.size()) // 2 players × 1 card + 1 trump
        }

        @Test
        fun `startRound sets no trump in last round when deck empties during dealing`() {

            val cardsInDeck = manager.deck.size()
            val playersCount = manager.players.size
            val lastRoundNumber = cardsInDeck / playersCount

            manager.startRound(lastRoundNumber)

            assertNull(manager.trumpCard)
            assertNull(manager.trumpSuit)
            assertEquals(0, manager.deck.size())
        }

        @Test
        fun `startRound resets game state`() {
            manager.currentTrickNumber = 5
            manager.playedCards.add(alice to Card(Suit.RED, 1)) // Use player-card pair

            manager.startRound(2)

            assertEquals(0, manager.currentTrickNumber)
            assertTrue(manager.playedCards.isEmpty())
        }

        @Test
        fun `startRound prints trump information`() {
            val outputStream = ByteArrayOutputStream()
            System.setOut(PrintStream(outputStream))

            manager.startRound(1)
            System.setOut(originalOut)

            val output = outputStream.toString()
            assertTrue(output.contains("Trumpf:") && (output.contains(manager.trumpSuit?.name ?: "Kein Trumpf")))
        }
    }

}
