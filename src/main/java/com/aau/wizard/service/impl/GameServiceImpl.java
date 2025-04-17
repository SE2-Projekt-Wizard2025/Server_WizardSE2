package com.aau.wizard.service.impl;

import com.aau.wizard.dto.CardDto;
import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.model.Game;
import com.aau.wizard.model.Player;
import com.aau.wizard.service.interfaces.GameService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameServiceImpl implements GameService {
    /**
     * A map that holds all the current active games
     */
    private final Map<String, Game> games = new HashMap<>();


    @Override
    public GameResponse joinGame(GameRequest request) {
        // check if there is a game, if not create one with the given id
        Game game = games.computeIfAbsent(request.getGameId(), Game::new);

        if(isPlayerExistent(game, request)) {
            Player newPlayer = new Player(request.getPlayerId(), request.getPlayerName());
            game.getPlayers().add(newPlayer);
        }

        return createGameResponse(game, request.getPlayerId());
    }

    private GameResponse createGameResponse(Game game, String requestingPlayerId) {
        List<PlayerDto> playerDtos = game.getPlayers().stream()
                .map(PlayerDto::from)
                .toList();

        Player requestingPlayer = game.getPlayerById(requestingPlayerId);

        List<CardDto> handCards = requestingPlayer != null
                ? requestingPlayer.getHandCards().stream()
                .map(CardDto::from)
                .toList()
                : List.of();

        return new GameResponse(
                game.getGameId(),
                game.getStatus(),
                game.getCurrentPlayerId(),
                playerDtos,
                handCards,
                null // lastPlayedCard can be set here later on
        );
    }

    private boolean isPlayerExistent(Game game, GameRequest request) {
        return game.getPlayerById(request.getPlayerId()) == null;
    }
}
