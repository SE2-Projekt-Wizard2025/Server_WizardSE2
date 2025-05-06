package com.aau.wizard.core.rules

import com.aau.wizard.core.model.PlayerState
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested

class BiddingRulesTest {

    @Nested
    inner class CalculateScoresTests {
        @Test
        fun `correct bid gives 20 plus 10 per trick`() {
            val player = PlayerState("p1", "Alice").apply {
                bid = 3
                tricksWon = 3
                score = 50
            }

            BiddingRules.calculateScores(listOf(player))

            assertEquals(50 + 20 + (10 * 3), player.score) // 50 + 50 = 100
        }

        @Test
        fun `wrong bid deducts 10 per trick difference`() {
            val player = PlayerState("p2", "Bob").apply {
                bid = 2
                tricksWon = 4
                score = 30
            }

            BiddingRules.calculateScores(listOf(player))

            assertEquals(30 - (10 * 2), player.score) // 30 - 20 = 10
        }

        @Test
        fun `zero bid correct gives exactly 20 points`() {
            val player = PlayerState("p3", "Charlie").apply {
                bid = 0
                tricksWon = 0
                score = 0
            }

            BiddingRules.calculateScores(listOf(player))

            assertEquals(20, player.score)
        }

        @Test
        fun `zero bid wrong gives negative points`() {
            val player = PlayerState("p4", "Dana").apply {
                bid = 0
                tricksWon = 1
                score = 15
            }

            BiddingRules.calculateScores(listOf(player))

            assertEquals(15 - 10, player.score) // 15 - 10 = 5
        }

        @Test
        fun `handles multiple players simultaneously`() {
            val players = listOf(
                PlayerState("p1", "Alice").apply { bid = 2; tricksWon = 2; score = 0 },
                PlayerState("p2", "Bob").apply { bid = 3; tricksWon = 1; score = 10 },
                PlayerState("p3", "Charlie").apply { bid = 0; tricksWon = 0; score = 5 }
            )

            BiddingRules.calculateScores(players)

            assertEquals(40, players[0].score)   // 20 + 20
            assertEquals(10 - 20, players[1].score) // -10
            assertEquals(5 + 20, players[2].score)  // 25
        }

        @Test
        fun `score accumulates over multiple rounds`() {
            val player = PlayerState("p5", "Eve").apply {
                bid = 1
                tricksWon = 1
                score = 100
            }

            BiddingRules.calculateScores(listOf(player))
            BiddingRules.calculateScores(listOf(player))

            assertEquals(100 + 30 + 30, player.score) // 100 + 30 + 30 = 160
        }

        @Test
        fun `exact negative difference calculation`() {
            val testCases = listOf(
                Triple(5, 2, -30),  // bid 5, won 2 → diff 3 → -30
                Triple(1, 4, -30),   // bid 1, won 4 → diff 3 → -30
                Triple(3, 3, 50)    // bid 3, won 3 → 20 + 30 = 50
            )

            testCases.forEach { (bid, won, expected) ->
                val player = PlayerState("p$bid", "Test").apply {
                    this.bid = bid
                    tricksWon = won
                    score = 0
                }

                BiddingRules.calculateScores(listOf(player))
                assertEquals(expected, player.score)
            }
        }
    }
}