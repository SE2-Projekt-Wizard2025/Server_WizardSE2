package com.aau.wizard.controller;

import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.service.interfaces.GameService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller that handles game-related messages from clients.
 * This class maps incoming STOMP messages to service layer calls and broadcasts
 * the resulting game state to all subscribed clients.
 */
@Controller
public class GameWebSocketController {
    private final GameService gameService;

    /**
     * Injects the game service to delegate game logic operations.
     *
     * @param gameService the service handling core game logic
     */
    public GameWebSocketController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Handles incoming WebSocket messages to join a game.
     * <p>
     * Clients send a {@link GameRequest} to the "/app/game/join" endpoint.
     * The resulting {@link GameResponse} is broadcast to all clients subscribed to "/topic/game".
     *
     * @param gameRequest the request payload containing game and player info
     * @return the updated game state as a response
     */
    @MessageMapping("/game/join")
    @SendTo("/topic/game")
    public GameResponse joinGame(GameRequest gameRequest) {
        return gameService.joinGame(gameRequest);
    }
}
