package com.aau.wizard.core.cards

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.provider.EnumSource

class DeckUtilsTest {

    @Nested
    inner class GetTrumpSuitTests {
        @Test
        fun `returns null for empty list`() {
            assertNull(DeckUtils.getTrumpSuit(emptyList()))
        }

        @Test
        fun `returns null when no wizards present`() {
            val cards = listOf(
                Card(Suit.RED, 1),
                Card(Suit.YELLOW, 0), // Jester
                Card(Suit.BLUE, 5)
            )
            assertNull(DeckUtils.getTrumpSuit(cards))
        }

        @ParameterizedTest
        @EnumSource(Suit::class)
        fun `returns suit of first wizard found`(suit: Suit) {
            val cards = listOf(
                Card(Suit.GREEN, 5),
                Card(suit, 14), // Wizard
                Card(Suit.BLUE, 14) // Another wizard (should be ignored)
            )
            assertEquals(suit, DeckUtils.getTrumpSuit(cards))
        }

        @Test
        fun `works with multiple wizards of different suits`() {
            val expectedSuit = Suit.YELLOW
            val cards = listOf(
                Card(Suit.RED, 1),
                Card(expectedSuit, 14), // First wizard
                Card(Suit.BLUE, 14)    // Second wizard
            )
            assertEquals(expectedSuit, DeckUtils.getTrumpSuit(cards))
        }
    }

    @Nested
    inner class IsWizardTests {
        @Test
        fun `returns true for wizard card`() {
            assertTrue(DeckUtils.isWizard(Card(Suit.SPECIAL, 14)))
        }

        @Test
        fun `returns false for non-wizard cards`() {
            assertFalse(DeckUtils.isWizard(Card(Suit.RED, 1)))
            assertFalse(DeckUtils.isWizard(Card(Suit.YELLOW, 0))) // Jester
        }

        @ParameterizedTest
        @EnumSource(Suit::class)
        fun `returns true for wizard in any suit`(suit: Suit) {
            assertTrue(DeckUtils.isWizard(Card(suit, 14)))
        }
    }

    @Nested
    inner class IsJesterTests {
        @Test
        fun `returns true for jester card`() {
            assertTrue(DeckUtils.isJester(Card(Suit.SPECIAL, 0)))
        }

        @Test
        fun `returns false for non-jester cards`() {
            assertFalse(DeckUtils.isJester(Card(Suit.BLUE, 5)))
            assertFalse(DeckUtils.isJester(Card(Suit.GREEN, 14))) // Wizard
        }

        @ParameterizedTest
        @EnumSource(Suit::class)
        fun `returns true for jester in any suit`(suit: Suit) {
            assertTrue(DeckUtils.isJester(Card(suit, 0)))
        }
    }
}