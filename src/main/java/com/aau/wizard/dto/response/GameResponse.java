package com.aau.wizard.dto.response;

public class GameResponse {
    // TODO: Add further attributes
    private String gameId;

    /**
     * Additional response data associated with the game.
     * The payload content depends on the specific request type or game state
     * and can vary accordingly.
     */
    private Object payload;

    // TODO: Add further attributes to constructor
    public GameResponse(String gameId, Object payload) {
        this.gameId = gameId;
        this.payload = payload;
    }

    // TODO: Add further getters and setters accordingly
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
