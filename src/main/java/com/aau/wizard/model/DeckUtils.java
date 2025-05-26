package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardType;
import com.aau.wizard.model.enums.CardSuit;

import java.util.List;

public final class DeckUtils {

    private DeckUtils() {
    }

    public static CardSuit getTrumpSuit(List<Card> cards) {
        for (Card card : cards) {
            if (card.getType() == CardType.WIZARD) {
                return card.getSuit();
            }
        }
        return null;
    }

    // fixme extract into the card subclasses
    public static boolean isWizard(Card card) {
        return card.getType() == CardType.WIZARD;
    }

    public static boolean isJester(Card card) {
        return card.getType() == CardType.JESTER;
    }
}
