package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.CardType;

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
        return value + " of " + cardSuit.name().toLowerCase();
    }
}
