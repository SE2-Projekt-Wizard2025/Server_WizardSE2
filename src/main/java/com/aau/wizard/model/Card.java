package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardType;
import com.aau.wizard.model.enums.CardSuit;

import java.util.Objects;

// fixme extract Special/jester and special/wizard into classes for polymorphism
public class Card {
    private final CardSuit cardSuit;
    private final int value;
    private final CardType type;

    public Card(CardSuit cardSuit, int value) {
        this.cardSuit = cardSuit;
        this.value = value;
        if (value == 0) {
            this.type = CardType.JESTER;
        } else if (value == 14) {
            this.type = CardType.WIZARD;
        } else {
            this.type = CardType.NUMBER;
        }
    }

    public CardSuit getSuit() {
        return cardSuit;
    }

    public int getValue() {
        return value;
    }

    public CardType getType() {
        return type;
    }

    @Override
    public String toString() {
        switch (type) {
            case WIZARD:
                return "Wizard (" + cardSuit.name().toLowerCase() + ")";
            case JESTER:
                return "Jester (" + cardSuit.name().toLowerCase() + ")";
            default:
                return value + " of " + cardSuit.name().toLowerCase();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return value == card.value &&
                cardSuit == card.cardSuit &&
                type == card.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardSuit, value, type);
    }
}
