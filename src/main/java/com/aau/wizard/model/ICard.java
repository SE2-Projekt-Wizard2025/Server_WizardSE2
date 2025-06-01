package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.CardType;

public interface ICard {
    CardSuit getSuit();
    int getValue();
    CardType getType();
    String toString();
}
