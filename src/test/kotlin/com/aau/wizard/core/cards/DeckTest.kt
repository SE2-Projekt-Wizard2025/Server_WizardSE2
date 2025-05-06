package com.aau.wizard.core.cards

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.junit.jupiter.api.Nested


class DeckTest {
    private lateinit var deck: Deck

    @BeforeEach
    fun setUp() {
        deck = Deck()
    }

    @Test
    fun `initial deck size should be 60 cards`() {
        assertEquals(60, deck.size())
    }

    @Nested
    inner class ShuffleTests {
        @Test
        fun `shuffle should maintain deck size`() {
            val initialSize = deck.size()
            deck.shuffle()
            assertEquals(initialSize, deck.size())
        }


        @Test
        fun `shuffle should change card order`() {
            val firstFiveBefore = deck.draw(5)
            deck = Deck()
            deck.shuffle()
            val firstFiveAfter = deck.draw(5)

            assertNotEquals(firstFiveBefore, firstFiveAfter)
        }
    }

    @Nested
    inner class DrawTests {
        @Test
        fun `draw 1 card should reduce deck size by 1`() {
            val initialSize = deck.size()
            deck.draw(1)
            assertEquals(initialSize - 1, deck.size())
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 5, 10, 60])
        fun `draw valid amount should return correct number of cards`(amount: Int) {
            val cards = deck.draw(amount)
            assertEquals(amount, cards.size)
        }

        @ParameterizedTest
        @ValueSource(ints = [0, 61, 100])
        fun `draw invalid amount should throw exception`(amount: Int) {
            assertThrows<IllegalArgumentException> {
                deck.draw(amount)
            }
        }

        @Test
        fun `draw should return cards in order from top of deck`() {
            val firstCard = deck.draw(1).first()
            val secondCard = deck.draw(1).first()

            // Verify they're different cards (unless deck has duplicates, which it shouldn't)
            assertNotEquals(firstCard, secondCard)
        }

        @Test
        fun `draw all cards should empty the deck`() {
            val initialSize = deck.size()
            deck.draw(initialSize)
            assertEquals(0, deck.size())
        }

        @Test
        fun `draw after emptying deck should throw exception`() {
            deck.draw(deck.size())
            assertThrows<IllegalArgumentException> {
                deck.draw(1)
            }
        }
    }

    @Test
    fun `deck should contain correct distribution of cards`() {
        val allCards = mutableListOf<Card>()
        while (deck.size() > 0) {
            allCards.addAll(deck.draw(1))
        }

        val numberCards = allCards.count { it.type == CardType.NUMBER }
        val wizards = allCards.count { it.type == CardType.WIZARD }
        val jesters = allCards.count { it.type == CardType.JESTER }

        assertEquals(52, numberCards)
        assertEquals(4, wizards)
        assertEquals(4, jesters)
    }
}