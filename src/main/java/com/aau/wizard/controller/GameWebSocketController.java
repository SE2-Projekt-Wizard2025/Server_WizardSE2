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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * WebSocket controller that handles game-related messages from clients.
 * This class maps incoming STOMP messages to service layer calls and broadcasts
 * the resulting game state to all subscribed clients.
 */
@Controller
public class GameWebSocketController {
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(GameWebSocketController.class);
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
     * //@return the updated game state as a response
     */
    @MessageMapping("/game/join")
    public void joinGame(GameRequest gameRequest) {
        logger.info("Received join request from: {} (ID: {}) for Game: {}",
                gameRequest.getPlayerName(), gameRequest.getPlayerId(), gameRequest.getGameId());

        GameResponse response = gameService.joinGame(gameRequest);
        messagingTemplate.convertAndSend("/topic/game", response);
    }

    @MessageMapping("/game/start")
    public void startGame(@Payload String gameId) {
        if (gameId != null && gameId.startsWith("\"") && gameId.endsWith("\"")) {
            gameId = gameId.substring(1, gameId.length() - 1);
        }

        logger.info("Start game with ID: {}", gameId);
        gameService.startGame(gameId); // keine Rückgabe → personalisierte Nachrichten werden dort verschickt
    }

    @MessageMapping("/game/predict")
    public void handlePrediction(PredictionRequest request) {
        try {
            GameResponse response = gameService.makePrediction(request);
            messagingTemplate.convertAndSend("/topic/game/" + request.getPlayerId(), response);
        } catch (IllegalArgumentException e) {
            messagingTemplate.convertAndSend(
                    "/topic/errors/" + request.getPlayerId(),
                    e.getMessage()
            );
        }
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

    /**
     *Behandelt Anfragen, um nach der Scoreboard-Anzeige zur nächsten Runde fortzufahren.
     * @param gameId Die ID des Spiels, das fortgesetzt werden soll.
     */
    @MessageMapping("/game/proceedToNextRound")
    public void proceedToNextRound(@Payload String gameId) {

        if (gameId != null && gameId.startsWith("\"") && gameId.endsWith("\"")) {
            gameId = gameId.substring(1, gameId.length() - 1);
        }
        logger.info("Anfrage erhalten, zur nächsten Runde für Spiel-ID {} fortzufahren.", gameId);
        gameService.proceedToNextRound(gameId);
    }

}
