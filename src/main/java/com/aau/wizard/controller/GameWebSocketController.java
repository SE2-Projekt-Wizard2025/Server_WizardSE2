package com.aau.wizard.controller;

import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.service.interfaces.GameService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.aau.wizard.dto.request.PredictionRequest;

import java.util.List;


/**
 * WebSocket controller that handles game-related messages from clients.
 * This class maps incoming STOMP messages to service layer calls and broadcasts
 * the resulting game state to all subscribed clients.
 */
@Controller
public class GameWebSocketController {
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;
    /**
     * Injects the game service to delegate game logic operations.
     *
     * @param gameService the service handling core game logic
     */
    public GameWebSocketController(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
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
    public void joinGame(GameRequest gameRequest) {
        System.out.println("Received join request from: " + gameRequest.getPlayerName() +
                " (ID: " + gameRequest.getPlayerId() + ") for Game: " + gameRequest.getGameId());

        GameResponse response = gameService.joinGame(gameRequest);
        messagingTemplate.convertAndSend("/topic/game", response);
    }

    @MessageMapping("/game/start")
    @SendTo("/topic/game")
    public GameResponse startGame(@Payload String gameId) {
        if (gameId != null && gameId.startsWith("\"") && gameId.endsWith("\"")) {
            gameId = gameId.substring(1, gameId.length() - 1);
        }

        System.out.println("Start game with ID: " + gameId);
        return gameService.startGame(gameId);
    }

    @MessageMapping("/game/predict")
    @SendTo("/topic/game")
    public GameResponse handlePrediction(PredictionRequest request) {
        return gameService.makePrediction(request);
    }

    @MessageMapping("/game/play")
    @SendTo("/topic/game")
    public GameResponse playCard(GameRequest request) {
        return gameService.playCard(request);
    }

    @MessageMapping("/game/{gameId}/scoreboard")
    @SendTo("/topic/game/{gameId}/scoreboard")
    public List<PlayerDto> sendScoreboard(@DestinationVariable String gameId) {
        return gameService.getScoreboard(gameId);
    }

}
