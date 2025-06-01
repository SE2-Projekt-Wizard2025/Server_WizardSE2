package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardSuit;

public class CardFactory {
    public static ICard createCard(CardSuit suit, int value) {
        if (value == 0) {
            return new JesterCard(suit);
        } else if (value == 14) {
            return new WizardCard(suit);
        } else {
            return new NumberCard(suit, value);
        }
    }
}
