package com.aau.wizard.service.impl;

import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.model.Game;
import com.aau.wizard.service.interfaces.GameService;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {
    /**
     * Starts a new game session based on the provided game request.
     *
     * @param request Contains information needed to initiate the game session, such as player data or game settings.
     * @return A {@link GameResponse} object including the newly created game's unique identifier and an optional payload.
     *         TODO: Further attributes should be added to this JavaDoc when they are implemented
     */
    @Override
    public GameResponse startGame(GameRequest request) {
        Game game = new Game();
        // TODO: Add further logic to start game

        return new GameResponse(game.getGameId(), null);
    }
}
