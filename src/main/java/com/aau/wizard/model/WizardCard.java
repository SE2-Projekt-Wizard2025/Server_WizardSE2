package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.CardType;

import java.util.Objects;

public class WizardCard extends AbstractSpecialCard {
    public WizardCard(CardSuit cardSuit) {
        super(cardSuit);
    }

    @Override
    public int getValue() {
        return 14;
    }

    @Override
    public CardType getType() {
        return CardType.WIZARD;
    }

    @Override
    public String toString() {
        return "WIZARD";
    }
    public static boolean isWizard(ICard card) {
        return card != null && card.getType() == CardType.WIZARD; //null check wird gebraucht
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass());
    }
}
