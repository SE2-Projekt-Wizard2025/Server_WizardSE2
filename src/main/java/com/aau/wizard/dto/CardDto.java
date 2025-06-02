package com.aau.wizard.dto;

import com.aau.wizard.model.ICard;
import com.aau.wizard.model.Player;
import com.aau.wizard.util.CollectionUtils;

import java.util.List;

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

    /**
     * Maps a {@link ICard} domain object to a {@link CardDto} for data transfer.
     * <p>
     * Converts enum values (color, value, type) to their string representations using {@code name()}.
     *
     * @param card the {@link ICard} to map (must not be {@code null})
     * @return the corresponding {@link CardDto}
     * @throws NullPointerException if the card or any of its fields is {@code null}
     */
    public static CardDto from(ICard card) {
        return new CardDto(
                card.getSuit().name(),
                Integer.toString(card.getValue()),
                card.getType().name()
        );
    }

    /**
     * Safely maps a player's hand cards to a list of {@link CardDto}.
     * <p>
     * If the player is {@code null} or their hand cards are {@code null},
     * an empty list is returned instead of throwing a {@link NullPointerException}.
     *
     * @param player the player whose hand cards should be mapped (may be {@code null})
     * @return a list of mapped {@link CardDto} objects or an empty list if the player or their cards are null
     */
    public static List<CardDto> safeFromPlayer(Player player) {
        if (player == null) return List.of();
        return CollectionUtils.mapOrEmpty(player.getHandCards(), CardDto::from);
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
