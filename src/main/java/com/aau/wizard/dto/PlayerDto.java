package com.aau.wizard.dto;

import com.aau.wizard.model.Player;

import java.util.List;

/**
 * DTO for sending basic player information to the client.
 */
public class PlayerDto {
    private String playerId;
    private String playerName;
    private int score;
    private boolean ready;
    private Integer prediction;
    private int tricksWon;
    private List<Integer> roundScores; // Punkte für jede Runde

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

    /**
     * Converts a {@link Player} domain object to a {@link PlayerDto} for data transfer.
     * <p>
     * Extracts basic player information such as ID, name, score, and ready state.
     *
     * @param p the {@link Player} instance to convert (must not be {@code null})
     * @return the corresponding {@link PlayerDto}
     * @throws NullPointerException if the player or any of its required fields is {@code null}
     */
    public static PlayerDto from(Player p) {
        PlayerDto dto = new PlayerDto(p.getPlayerId(), p.getName(), p.getScore(), p.isReady());
        dto.setPrediction(p.getPrediction());
        dto.setRoundScores(p.getRoundScores());
        return dto;
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

    public Integer getPrediction(){ return prediction;}

    public void setPrediction(Integer prediction){this.prediction=prediction;}

    public int getTricksWon() {
        return tricksWon;
    }

    public void setTricksWon(int tricksWon) {
        this.tricksWon = tricksWon;
    }

    public List<Integer> getRoundScores() {
        return roundScores;
    }

    public void setRoundScores(List<Integer> roundScores) {
        this.roundScores = roundScores;
    }
}
