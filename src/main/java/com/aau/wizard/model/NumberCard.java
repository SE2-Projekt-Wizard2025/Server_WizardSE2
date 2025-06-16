package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.CardType;

import java.util.Objects;

public class NumberCard extends AbstractSpecialCard {
    private final int value;

    public NumberCard(CardSuit cardSuit, int value) {
        super(cardSuit);
        if (value <= 0 || value >= 14) {
            throw new IllegalArgumentException("Number card value must be between 1 and 13");
        }
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public CardType getType() {
        return CardType.NUMBER;
    }

    @Override
    public String toString() {
        return getSuit().name() + "_" + getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumberCard that = (NumberCard) o;
        return getValue() == that.getValue() && getSuit() == that.getSuit();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSuit(), getValue());
    }
}
