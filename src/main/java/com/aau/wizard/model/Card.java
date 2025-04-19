package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardColor;
import com.aau.wizard.model.enums.CardType;
import com.aau.wizard.model.enums.CardValue;

public class Card {
    private CardColor color;
    private CardValue value;
    private CardType type;

    public Card(CardColor color, CardValue value, CardType type) {
        this.color = color;
        this.value = value;
        this.type = type;
    }

    public CardColor getColor() {
        return color;
    }

    public void setColor(CardColor color) {
        this.color = color;
    }

    public CardValue getValue() {
        return value;
    }

    public void setValue(CardValue value) {
        this.value = value;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }
}
