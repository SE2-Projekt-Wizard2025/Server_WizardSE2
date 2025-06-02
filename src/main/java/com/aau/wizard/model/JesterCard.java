package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.CardType;

public class JesterCard extends AbstractSpecialCard {
    public JesterCard(CardSuit cardSuit) {
        super(cardSuit);
    }

    @Override
    public int getValue() {
        return 0;
    }

    @Override
    public CardType getType() {
        return CardType.JESTER;
    }

    @Override
    public String toString() {
        return "Jester (" + cardSuit.name().toLowerCase() + ")";
    }

    public static boolean isJester(ICard card) {
        return card.getType() == CardType.JESTER;
    }
}
