package com.aau.wizard.controller;

import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.service.interfaces.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(GameWebSocketController.class);
    private final GameService gameService;

    public GameWebSocketController(GameService gameService) {
        this.gameService = gameService;
    }

    @MessageMapping("/game/play")
    @SendTo("/topic/game")
    public GameResponse startGame(@Payload GameRequest gameRequest) {
        if (gameRequest == null) {
            logger.error("GameRequest ist NULL! Stelle sicher, dass du eine gültige Nachricht sendest.");
            throw new IllegalArgumentException("Game request cannot be null");
        }

        logger.info("📩 Empfangene WebSocket-Nachricht: {}", gameRequest);

        return gameService.startGame(gameRequest);
    }
}