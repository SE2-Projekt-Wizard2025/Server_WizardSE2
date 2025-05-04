package com.aau.wizard.core.logic

import com.aau.wizard.core.cards.*
import com.aau.wizard.core.game.*
import com.aau.wizard.core.model.PlayerState
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameManagerTest {

    private lateinit var alice: PlayerState
    private lateinit var bob: PlayerState
    private lateinit var manager: GameManager

    @BeforeEach
    fun setup() {
        alice = PlayerState(playerId = "p1", name = "Alice")
        bob = PlayerState(playerId = "p2", name = "Bob")
        manager = GameManager(listOf(alice, bob))
    }

    @Test
    fun `player must follow suit if able`() {
        // Setup: Alice spielt RED, Bob hat RED und GREEN
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
}
