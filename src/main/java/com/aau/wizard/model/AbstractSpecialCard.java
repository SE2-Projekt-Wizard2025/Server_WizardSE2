package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardSuit;

import java.util.Objects;

public abstract class AbstractSpecialCard implements ICard {
    protected final CardSuit cardSuit;

    public AbstractSpecialCard(CardSuit cardSuit) {
        this.cardSuit = cardSuit;
    }

    @Override
    public CardSuit getSuit() {
        return cardSuit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ICard)) return false;
        ICard that = (ICard) o;
        return this.getType() == that.getType()
                && this.getValue() == that.getValue()
                && this.getSuit() == that.getSuit();
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardSuit, getValue(), getType());
    }
}
