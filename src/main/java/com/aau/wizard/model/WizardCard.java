package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.CardType;

import java.util.Objects;

public class WizardCard implements ICard {
    private final CardSuit cardSuit;
    private final CardType type = CardType.WIZARD;

    public WizardCard(CardSuit cardSuit) {
        this.cardSuit = cardSuit;
    }

    @Override
    public CardSuit getSuit() {
        return cardSuit;
    }

    @Override
    public int getValue() {
        return 14;
    }

    @Override
    public CardType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Wizard (" + cardSuit.name().toLowerCase() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WizardCard)) return false;
        WizardCard that = (WizardCard) o;
        return cardSuit == that.cardSuit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardSuit);
    }
}
