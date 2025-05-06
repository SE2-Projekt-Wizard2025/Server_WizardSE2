package com.aau.wizard.core.cards

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.provider.EnumSource

class CardTest {

    @Nested
    inner class CardTypeDetection {
        @Test
        fun `number card has correct type`() {
            val card = Card(Suit.RED, 5)
            assertEquals(CardType.NUMBER, card.type)
        }


        @Test
        fun `jester card has correct type`() {
            val card = Card(Suit.YELLOW, 0)
            assertEquals(CardType.JESTER, card.type)
        }

        @Test
        fun `wizard card has correct type`() {
            val card = Card(Suit.BLUE, 14)
            assertEquals(CardType.WIZARD, card.type)
        }
    }

    @Nested
    inner class CardToString {
        @Test
        fun `number card string representation`() {
            val card = Card(Suit.GREEN, 7)
            assertEquals("7 of green", card.toString())
        }

        @Test
        fun `jester card string representation`() {
            val card = Card(Suit.SPECIAL, 0)
            assertEquals("Jester (special)", card.toString())
        }

        @Test
        fun `wizard card string representation`() {
            val card = Card(Suit.RED, 14)
            assertEquals("Wizard (red)", card.toString())
        }
    }


    @Nested
    inner class CardProperties {
        @ParameterizedTest
        @EnumSource(Suit::class)
        fun `card has correct suit`(suit: Suit) {
            val card = Card(suit, 3)
            assertEquals(suit, card.suit)
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 5, 13])
        fun `number card has correct value`(value: Int) {
            val card = Card(Suit.BLUE, value)
            assertEquals(value, card.value)
            assertEquals(CardType.NUMBER, card.type)
        }

        @Test
        fun `jester has correct value`() {
            val card = Card(Suit.YELLOW, 0)
            assertEquals(0, card.value)
            assertEquals(CardType.JESTER, card.type)
        }

        @Test
        fun `wizard has correct value`() {
            val card = Card(Suit.SPECIAL, 14)
            assertEquals(14, card.value)
            assertEquals(CardType.WIZARD, card.type)
        }
    }

    @Nested
    inner class EdgeCases {
        @Test
        fun `minimum number card value`() {
            val card = Card(Suit.RED, 1)
            assertEquals(1, card.value)
            assertEquals(CardType.NUMBER, card.type)
        }

        @Test
        fun `maximum number card value`() {
            val card = Card(Suit.GREEN, 13)
            assertEquals(13, card.value)
            assertEquals(CardType.NUMBER, card.type)
        }


        @Test
        fun `special suit with number card`() {
            val card = Card(Suit.SPECIAL, 5)
            assertEquals(Suit.SPECIAL, card.suit)
            assertEquals(CardType.NUMBER, card.type)
        }
    }

    @Nested
    inner class ToStringCoverage {
        @Test
        fun `toString for WIZARD card`() {
            val wizardCard = Card(Suit.RED, 14)
            assertEquals("Wizard (red)", wizardCard.toString())
        }

        @Test
        fun `toString for JESTER card`() {
            val jesterCard = Card(Suit.YELLOW, 0)
            assertEquals("Jester (yellow)", jesterCard.toString())
        }

        @Test
        fun `toString for NUMBER card`() {
            val numberCard = Card(Suit.BLUE, 5)
            assertEquals("5 of blue", numberCard.toString())
        }
    }
}

class SuitTest {
    @Test
    fun `all suit values exist`() {
        val values = Suit.values()
        assertEquals(5, values.size)
        assertTrue(values.contains(Suit.RED))
        assertTrue(values.contains(Suit.YELLOW))
        assertTrue(values.contains(Suit.BLUE))
        assertTrue(values.contains(Suit.GREEN))
        assertTrue(values.contains(Suit.SPECIAL))
    }
}


class CardTypeTest {
    @Test
    fun `all card type values exist`() {
        val values = CardType.values()
        assertEquals(3, values.size)
        assertTrue(values.contains(CardType.NUMBER))
        assertTrue(values.contains(CardType.WIZARD))
        assertTrue(values.contains(CardType.JESTER))
    }
}