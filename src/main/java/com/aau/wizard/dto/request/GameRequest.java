package com.aau.wizard.dto.request;

public class GameRequest {
    private String gameId;
    private String playerId;
    private String playerName;
    private String card;
    private String action;

    /**
     * No args constructor needed for Jackson / JSON deserialization
     */
    public GameRequest() {}

    /**
     * Convenience constructor to create a GameRequest manually, e.g. in unit tests.
     *
     * @param gameId   the ID of the game
     * @param playerId the ID of the player
     */
    public GameRequest(String gameId, String playerId) {
        this.gameId = gameId;
        this.playerId = playerId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
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

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
