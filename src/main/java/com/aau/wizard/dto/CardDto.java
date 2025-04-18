package com.aau.wizard.dto;

import com.aau.wizard.model.Card;

/**
 * DTO for sending basic card information to the client.
 */
public class CardDto {
    private String color;
    private String value;
    private String type;

    /**
     * No args constructor needed for Jackson / JSON deserialization
     */
    public CardDto() {}

    public CardDto(String color, String value, String type) {
        this.color = color;
        this.value = value;
        this.type = type;
    }

    public static CardDto from(Card card) {
        return new CardDto(
                card.getColor().name(),
                card.getValue().name(),
                card.getType().name()
        );
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
