package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardSuit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<ICard> cards = new ArrayList<>();

    public Deck() {
        initializeDeck();
    }

    private void initializeDeck() {
        // Standardkarten (1–13 in jeder Farbe außer SPECIAL)
        for (CardSuit cardSuit : CardSuit.values()) {
            if (cardSuit != CardSuit.SPECIAL) {
                for (int value = 1; value <= 13; value++) {
                    cards.add(CardFactory.createCard(cardSuit, value));
                }
            }
        }

        // Spezialkarten (4 Wizard + 4 Jester)
        for (int i = 0; i < 4; i++) {
            cards.add(CardFactory.createCard(CardSuit.SPECIAL, 14));// Wizard
            cards.add(CardFactory.createCard(CardSuit.SPECIAL, 0));//Jester
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public List<ICard> draw(int amount) {
        if (amount < 1 || amount > cards.size()) {
            throw new IllegalArgumentException("Cannot draw " + amount + " cards from deck");
        }

        List<ICard> drawn = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            drawn.add(cards.remove(0));
        }
        return drawn;
    }

    public int size() {
        return cards.size();
    }
}
