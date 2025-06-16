package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.CardType;

public interface ICard {
    CardSuit getSuit();
    int getValue();
    CardType getType();
    String toString();


    static ICard fromString(String cardString) {
        if (cardString == null || cardString.trim().isEmpty()) {
            throw new IllegalArgumentException("Karten-String darf nicht leer sein.");
        }

        String upperCaseCardString = cardString.trim().toUpperCase();


        if (upperCaseCardString.equals("WIZARD")) {

            return new WizardCard(CardSuit.SPECIAL);
        }
        if (upperCaseCardString.equals("JESTER")) {

            return new JesterCard(CardSuit.SPECIAL);
        }


        String[] parts = upperCaseCardString.split("_");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Ungültiges Karten-Format. Erwartet: 'FARBE_WERT' (z.B. 'RED_10'), aber erhalten: " + cardString);
        }

        try {
            CardSuit suit = CardSuit.valueOf(parts[0]);
            int value = Integer.parseInt(parts[1]);

            return new NumberCard(suit, value);
        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException("Ungültige Karten-Farbe oder Wert im String: " + cardString, e);
        }
    }
}
