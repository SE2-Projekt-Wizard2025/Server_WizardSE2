package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.CardType;

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
        return "Wizard (" + cardSuit.name().toLowerCase() + ")";
    }

    public static boolean isWizard(ICard card) {
        return card.getType() == CardType.WIZARD;
    }
}
