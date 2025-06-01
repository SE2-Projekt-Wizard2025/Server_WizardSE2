package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.CardType;

import java.util.Objects;

public class NumberCard implements ICard {
    private final CardSuit cardSuit;
    private final int value;
    private final CardType type = CardType.NUMBER;

    public NumberCard(CardSuit cardSuit, int value) {
        if (value <= 0 || value >= 14) {
            throw new IllegalArgumentException("Number card value must be between 1 and 13");
        }
        this.cardSuit = cardSuit;
        this.value = value;
    }

    @Override
    public CardSuit getSuit() {
        return cardSuit;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public CardType getType() {
        return type;
    }

    @Override
    public String toString() {
        return value + " of " + cardSuit.name().toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberCard)) return false;
        NumberCard that = (NumberCard) o;
        return value == that.value && cardSuit == that.cardSuit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardSuit, value);
    }
}
