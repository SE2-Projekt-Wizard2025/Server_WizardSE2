package com.aau.wizard.controller;

import com.aau.wizard.GameExceptions;
import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.service.impl.GameServiceImpl;
import com.aau.wizard.service.interfaces.GameService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.aau.wizard.dto.request.PredictionRequest;
import java.util.List;
import com.aau.wizard.GameExceptions.GameNotFoundException;
import com.aau.wizard.GameExceptions.PlayerNotFoundException;
import com.aau.wizard.GameExceptions.InvalidTurnException;
import com.aau.wizard.GameExceptions.InvalidPredictionException;
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
        try {
            GameResponse response = gameService.joinGame(gameRequest);
            messagingTemplate.convertAndSend("/topic/game", response);
         } catch (Exception e) {
            messagingTemplate.convertAndSend(
                    "/topic/errors/" + gameRequest.getPlayerId(),
                    "Fehler beim Beitritt zum Spiel: " + e.getMessage()
            );
        }
    }

    @MessageMapping("/game/start")
    public void startGame(@Payload String gameId) {
        String cleanGameId = (gameId != null && gameId.startsWith("\"") && gameId.endsWith("\"")) ?
                gameId.substring(1, gameId.length() - 1) : gameId;

        try {
            gameService.startGame(cleanGameId);
        } catch (GameNotFoundException | GameExceptions.GameStartException e) {
            messagingTemplate.convertAndSend(
                    "/topic/errors",
                    "Fehler beim Starten des Spiels " + cleanGameId + ": " + e.getMessage()
            );
        } catch (Exception e) {
            messagingTemplate.convertAndSend(
                    "/topic/errors",
                    "Ein unerwarteter Fehler ist beim Starten des Spiels aufgetreten."
            );
        }
    }

    @MessageMapping("/game/predict")
    public void handlePrediction(PredictionRequest request) {
        try {
            GameResponse response = gameService.makePrediction(request);
            messagingTemplate.convertAndSend("/topic/game/" + request.getPlayerId(), response);
         } catch (GameNotFoundException | PlayerNotFoundException | InvalidTurnException | InvalidPredictionException e) {
            messagingTemplate.convertAndSend(
                    "/topic/errors/" + request.getPlayerId(),
                    e.getMessage()
            );
        } catch (Exception e) {
           messagingTemplate.convertAndSend(
                    "/topic/errors/" + request.getPlayerId(),
                    "Ein unerwarteter Fehler ist aufgetreten."
            );
        }
    }

    @MessageMapping("/game/play")
    @SendTo("/topic/game")
    public GameResponse playCard(GameRequest request) {
        try {
             return gameService.playCard(request);
        } catch (GameNotFoundException | GameExceptions.GameAlreadyEndedException |
                 GameExceptions.GameNotActiveException |
                 PlayerNotFoundException | InvalidTurnException | GameExceptions.RoundLogicException |
                 GameExceptions.CardNotInHandException e) {
           messagingTemplate.convertAndSend(
                    "/topic/errors/" + request.getPlayerId(),
                    e.getMessage()
            );
            return null;
        } catch (Exception e) {
           messagingTemplate.convertAndSend(
                    "/topic/errors/" + request.getPlayerId(),
                    "Ein unerwarteter Fehler ist beim Spielen der Karte aufgetreten."
            );
            return null;
        }
    }

    @MessageMapping("/game/{gameId}/scoreboard")
    @SendTo("/topic/game/{gameId}/scoreboard")
    public List<PlayerDto> sendScoreboard(@DestinationVariable String gameId) {
        try {
           return gameService.getScoreboard(gameId);
        } catch (GameNotFoundException e) {
            messagingTemplate.convertAndSend(
                    "/topic/errors/" + gameId,
                    "Fehler beim Abrufen des Scoreboards: " + e.getMessage()
            );
            return List.of();
        } catch (Exception e) {
             messagingTemplate.convertAndSend(
                    "/topic/errors/" + gameId,
                    "Ein unerwarteter Fehler ist beim Abrufen des Scoreboards aufgetreten."
            );
            return List.of();
        }
    }


    /**
     *Behandelt Anfragen, um nach der Scoreboard-Anzeige zur nächsten Runde fortzufahren.
     * @param gameId Die ID des Spiels, das fortgesetzt werden soll.
     */
    @MessageMapping("/game/proceedToNextRound")
    public void proceedToNextRound(@Payload String gameId) {

        String cleanGameId = (gameId != null && gameId.startsWith("\"") && gameId.endsWith("\"")) ?
                gameId.substring(1, gameId.length() - 1) : gameId;
        try {
            gameService.proceedToNextRound(cleanGameId);
       } catch (GameNotFoundException | GameExceptions.RoundLogicException | GameExceptions.RoundProgressionException e) {
            messagingTemplate.convertAndSend(
                    "/topic/errors/" + cleanGameId,
                    "Fehler beim Fortfahren zur nächsten Runde in Spiel " + cleanGameId + ": " + e.getMessage()
            );
        } catch (Exception e) {
            messagingTemplate.convertAndSend(
                    "/topic/errors/" + cleanGameId,
                    "Ein unerwarteter Fehler ist beim Fortfahren zur nächsten Runde aufgetreten."
            );
        }
    }

    /**
     * Behandelt die Anfrage eines Clients, das Spiel für alle Teilnehmer abzubrechen.
     * @param jsonGameId Die ID des Spiels als JSON-formatierter String.
     */
    @MessageMapping("/game/abort")
    public void handleGameAbort(@Payload String jsonGameId) {

        String gameId = (jsonGameId != null && jsonGameId.startsWith("\"") && jsonGameId.endsWith("\"")) ?
                jsonGameId.substring(1, jsonGameId.length() - 1) : jsonGameId;

        try {
            gameService.abortGame(gameId);

        } catch (GameExceptions.GameNotFoundException e) {
            logger.error("Fehler beim Abbrechen von Spiel {}: {}", sanitize(gameId), e.getMessage());

        } catch (Exception e) {
            logger.error("Unerwarteter Fehler beim Abbrechen von Spiel {}: {}", sanitize(gameId), e.getMessage(), e);
        }
    }

    @MessageMapping("/game/return-to-lobby")
    public void handleReturnToLobby(@Payload String jsonGameId) {
        String gameId = (jsonGameId != null && jsonGameId.startsWith("\"") && jsonGameId.endsWith("\"")) ?
                jsonGameId.substring(1, jsonGameId.length() - 1) : jsonGameId;

        try {
            gameService.signalReturnToLobby(gameId);
        } catch (Exception e) {
            logger.error("Fehler bei der Rückkehr zur Lobby für Spiel {}: {}", sanitize(gameId), e.getMessage(), e);
        }
    }

    private String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.replace('\n', '_').replace('\r', '_');
    }
}
