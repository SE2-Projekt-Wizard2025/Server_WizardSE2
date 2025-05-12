package com.aau.wizard.dto.request;

public class PredictionRequest {
    private String gameId;
    private String playerId;
    private int prediction;

    public PredictionRequest(){}

    public PredictionRequest(String gameId, String playerId, int prediction){
        this.gameId=gameId;
        this.playerId=playerId;
        this.prediction=prediction;
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

    public int getPrediction() {
        return prediction;
    }

    public void setPrediction(int prediction) {
        this.prediction = prediction;
    }
}
