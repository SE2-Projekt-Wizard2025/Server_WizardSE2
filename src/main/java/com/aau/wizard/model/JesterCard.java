package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.CardType;

import java.util.Objects;

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
        return "JESTER";
    }
    public static boolean isJester(ICard card) {
        return card.getType() == CardType.JESTER;
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
