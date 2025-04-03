package com.aau.wizard.controller;

import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.service.interfaces.GameService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {
    private final GameService gameService;

    public GameWebSocketController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Handles incoming WebSocket messages sent to "/game/play" endpoint.
     * Initiates a game session based on the provided {@link GameRequest}.
     *
     * @param gameRequest The request payload containing details required to start a game.
     * @return A {@link GameResponse} that is broadcasted to subscribers listening on "/topic/game".
     */
    @MessageMapping("/game/play")
    @SendTo("/topic/game")
    public GameResponse startGame(GameRequest gameRequest) {

        if(gameRequest == null){
            throw new IllegalArgumentException("Game request cannot be null");
        }

        return gameService.startGame(gameRequest);
    }
}
