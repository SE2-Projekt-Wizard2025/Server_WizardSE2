package com.aau.wizard.dto;

/**
 * DTO for sending basic card information to the client.
 */
public class CardDto {
    // TODO: Add attributes / getters / setters

    /**
     * No args constructor needed for Jackson / JSON deserialization
     */
    public CardDto() {}

    public static CardDto from(Object card) {
        // TODO: Replace later with actual mappings (see PlayerDto)
        return new CardDto();
    }
}
