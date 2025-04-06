package com.aau.wizard.dto.request;

public class GameRequest {
    // TODO: Add further attributes / getters / setters
    private String gameId;

    /**
     * No args constructor needed for Jackson
     */
    public GameRequest() {}

    public GameRequest(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
