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
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();}
