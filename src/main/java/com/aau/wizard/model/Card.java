

//Des is dei alter Code Elias ************************************************************************************************************

/*package com.aau.wizard.model;

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
*/

package com.aau.wizard.model;

import com.aau.wizard.model.enums.CardType;
import com.aau.wizard.model.enums.CardSuit;

public class Card {
    private final CardSuit cardSuit;
    private final int value;
    private final CardType type;

    public Card(CardSuit cardSuit, int value) {
        this.cardSuit = cardSuit;
        this.value = value;
        if (value == 0) {
            this.type = CardType.JESTER;
        } else if (value == 14) {
            this.type = CardType.WIZARD;
        } else {
            this.type = CardType.NUMBER;
        }
    }

    public CardSuit getSuit() {
        return cardSuit;
    }

    public int getValue() {
        return value;
    }

    public CardType getType() {
        return type;
    }

    @Override
    public String toString() {
        switch (type) {
            case WIZARD:
                return "Wizard (" + cardSuit.name().toLowerCase() + ")";
            case JESTER:
                return "Jester (" + cardSuit.name().toLowerCase() + ")";
            default:
                return value + " of " + cardSuit.name().toLowerCase();
        }
    }
}
