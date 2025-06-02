package com.aau.wizard.dto.request;

public abstract class BaseRequest {
    private String gameId;
    private String playerId;
    private String playerName;

    /**
     * No args constructor needed for Jackson / JSON deserialization
     */
    public BaseRequest() {}

    public BaseRequest(String gameId, String playerId) {
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
}
