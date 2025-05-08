

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

public class Card {
    private final Suit suit;
    private final int value;
    private final CardType type;

    public Card(Suit suit, int value) {
        this.suit = suit;
        this.value = value;
        if (value == 0) {
            this.type = CardType.JESTER;
        } else if (value == 14) {
            this.type = CardType.WIZARD;
        } else {
            this.type = CardType.NUMBER;
        }
    }

    public Suit getSuit() {
        return suit;
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
                return "Wizard (" + suit.name().toLowerCase() + ")";
            case JESTER:
                return "Jester (" + suit.name().toLowerCase() + ")";
            default:
                return value + " of " + suit.name().toLowerCase();
        }
    }
}
