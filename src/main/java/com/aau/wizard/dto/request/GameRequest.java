package com.aau.wizard.dto.request;

public class GameRequest extends BaseRequest {
    private String card;
    private String action;
    private Boolean isCheating;

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
        super(gameId, playerId);
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

    public Boolean getIsCheating() {
        return isCheating;
    }

    public void setIsCheating(Boolean isCheating) {
        this.isCheating = isCheating;
    }
}
