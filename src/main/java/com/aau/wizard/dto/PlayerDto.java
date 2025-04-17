package com.aau.wizard.dto;

import com.aau.wizard.model.Player;

/**
 * DTO for sending basic player information to the client.
 */
public class PlayerDto {
    private String playerId;
    private String playerName;
    private int score;
    private boolean ready;

    /**
     * No args constructor needed for Jackson / JSON deserialization
     */
    public PlayerDto() {}

    public PlayerDto(String playerId, String playerName, int score, boolean ready) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.score = score;
        this.ready = ready;
    }

    public static PlayerDto from(Player p) {
        return new PlayerDto(p.getPlayerId(), p.getName(), p.getScore(), p.isReady());
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
