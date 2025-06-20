package com.aau.wizard.service.interfaces;

import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.request.PredictionRequest;
import com.aau.wizard.dto.response.GameResponse;

import java.util.List;

public interface GameService {
    GameResponse joinGame(GameRequest request);
    GameResponse startGame(String gameId);
    boolean canStartGame(String gameId);
    GameResponse makePrediction(PredictionRequest request);
    List<PlayerDto> getScoreboard(String gameId);
    void processEndOfRound(String gameId);
    GameResponse playCard(GameRequest request);
    void proceedToNextRound(String gameId);
}



