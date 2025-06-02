package com.aau.wizard.dto.request;

public class PredictionRequest extends BaseRequest {
    private int prediction;

    /**
     * No args constructor needed for Jackson / JSON deserialization
     */
    public PredictionRequest(){}

    public PredictionRequest(String gameId, String playerId, int prediction){
        super(gameId, playerId);
        this.prediction = prediction;
    }

    public int getPrediction() {
        return prediction;
    }

    public void setPrediction(int prediction) {
        this.prediction = prediction;
    }
}
