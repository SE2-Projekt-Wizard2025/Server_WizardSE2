package com.aau.wizard.service.interfaces;

import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.request.PredictionRequest;
import com.aau.wizard.dto.response.GameResponse;

public interface GameService {
    GameResponse joinGame(GameRequest request);
    GameResponse startGame(String gameId);
    boolean canStartGame(String gameId);
    GameResponse makePrediction(PredictionRequest request);

}
