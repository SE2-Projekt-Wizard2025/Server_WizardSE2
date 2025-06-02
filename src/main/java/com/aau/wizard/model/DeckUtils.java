package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardType;
import com.aau.wizard.model.enums.CardSuit;

import java.util.List;

public final class DeckUtils {

    private DeckUtils() {
    }

    public static CardSuit getTrumpSuit(List<ICard> cards) {
        for (ICard card : cards) {
            if (card.getType() == CardType.WIZARD) {
                return card.getSuit();
            }
        }
        return null;
    }
}
