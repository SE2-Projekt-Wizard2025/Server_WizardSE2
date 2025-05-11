package com.aau.wizard.service.impl;
import com.aau.wizard.dto.CardDto;
import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.request.PredictionRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.model.Card;
import com.aau.wizard.model.Game;
import com.aau.wizard.model.Player;
import com.aau.wizard.service.interfaces.GameService;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aau.wizard.util.CollectionUtils.mapOrEmpty;

/**
 * Default implementation of the GameService interface.
 * Manages active games in memory and handles game-related logic like joining and tracking player state.
 */
@Service
public class GameServiceImpl implements GameService {
    /**
     * In-memory storage of all active games, keyed by their gameId.
     */
    private final Map<String, Game> games = new HashMap<>();
    private final Map<String, RoundServiceImpl> roundServices = new HashMap<>();


    /**
     * Handles a player joining a game. Creates the game if it doesn't exist,
     * adds the player if not already present, and returns the updated game state.
     *
     * @param request contains the gameId and player details
     * @return a GameResponse reflecting the current state of the game
     */
    @Override
    public GameResponse joinGame(GameRequest request) {
        // check if there is a game, if not create one with the given id
        Game game = games.computeIfAbsent(request.getGameId(), Game::new);
        addPlayerIfAbsent(game, request);

        return createGameResponse(game, request.getPlayerId(), null);
    }

    /**
     * Constructs a GameResponse for a given game and requesting player.
     * This includes visible player data and the requesting player's hand cards.
     *
     * @param game the game object to transform
     * @param requestingPlayerId the player for whom the response is built
     * @return a fully populated GameResponse
     */
    private GameResponse createGameResponse(Game game, String requestingPlayerId, Card trumpCard) {
        List<PlayerDto> playerDtos = mapOrEmpty(game.getPlayers(), PlayerDto::from);
        Player requestingPlayer = game.getPlayerById(requestingPlayerId);
        List<CardDto> handCards = CardDto.safeFromPlayer(requestingPlayer);

        return new GameResponse(
                game.getGameId(),
                game.getStatus(),
                game.getCurrentPlayerId(),
                playerDtos,
                handCards,
                null,// lastPlayedCard can be set here later on
                trumpCard != null ? CardDto.from(trumpCard) : null
        );
    }

    /**
     * Adds a new player to the game if they are not already part of it.
     *
     * @param game the game the player wants to join
     * @param request the request containing playerId and playerName
     */
    private void addPlayerIfAbsent(Game game, GameRequest request) {
        if (playerNotInGame(game, request)) {
            Player newPlayer = new Player(request.getPlayerId(), request.getPlayerName());
            game.getPlayers().add(newPlayer);
        }
    }

    /**
     * Checks whether the player is not yet part of the game.
     * Used to avoid duplicate joins.
     */
    private boolean playerNotInGame(Game game, GameRequest request) {
        return game.getPlayerById(request.getPlayerId()) == null;
    }

    @Override
    public GameResponse startGame(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Spiel nicht gefunden: " + gameId);
        }

        boolean started = game.startGame(); // ruft neue Methode aus Game.java auf
        if (!started) {
            throw new IllegalStateException("Spiel konnte nicht gestartet werden – evtl. zu wenig Spieler?");
        }

        RoundServiceImpl roundService = new RoundServiceImpl(game);
        roundService.startRound(1);//1 ist die Rundenanzahl — später noch dynamisch setzen
        Card trumpCard = roundService.trumpCard;
        CardDto trumpCardDto = trumpCard != null ? CardDto.from(trumpCard) : null;
        roundServices.put(gameId, roundService);

        return createGameResponse(game, game.getCurrentPlayerId(), trumpCard);
    }

    @Override
    public boolean canStartGame(String gameId) {
        Game game = games.get(gameId);
        return game != null && game.canStartGame();
    }


    /**
     * Returns the game instance associated with the given game ID.
     * <p>
     * <strong>Visible for testing only.</strong> This method should not be used in production logic,
     * but exists to allow test code to access internal game state (e.g. to inspect or manipulate players).
     *
     * @param gameId the unique identifier of the game
     * @return the Game instance if present; otherwise {@code null}
     */
    @VisibleForTesting
    public Game getGameById(String gameId) {
        return games.get(gameId);
    }

    @Override
    public GameResponse makePrediction(PredictionRequest request) {
        Game game = games.get(request.getGameId());
        if (game == null) {
            throw new IllegalArgumentException("Spiel nicht gefunden");
        }

        Player player = game.getPlayerById(request.getPlayerId());
        if (player == null) {
            throw new IllegalArgumentException("Spieler nicht gefunden");
        }

        int prediction = request.getPrediction();

        // Sonderregel: Letzter Spieler darf keine perfekte Summe vorhersagen
        List<Player> allPlayers = game.getPlayers();
        List<String> predictionOrder = game.getPredictionOrder();

        long alreadyPredicted = allPlayers.stream().filter(p -> p.getPrediction() != null).count();
        String expectedPlayerId = predictionOrder.get((int) alreadyPredicted);
        if (!expectedPlayerId.equals(player.getPlayerId())) {
            throw new IllegalStateException("Du bist noch nicht an der Reihe, bitte warte.");
        }

        boolean isLastPlayer=predictionOrder.indexOf(player.getPlayerId())==predictionOrder.size()-1;
        if (isLastPlayer) {
            int sumOfOtherPredictions = allPlayers.stream()
                    .filter(p -> !p.getPlayerId().equals(player.getPlayerId()))
                    .map(p -> p.getPrediction() != null ? p.getPrediction() : 0)
                    .reduce(0, Integer::sum);

            int totalTricks = player.getHandCards().size();

            if (sumOfOtherPredictions + prediction == totalTricks) {
                throw new IllegalArgumentException(
                        "Diese Vorhersage ergibt exakt die Anzahl der Stiche und ist damit verboten."
                );
            }
        }

        player.setPrediction(prediction);
        return createGameResponse(game, player.getPlayerId(), null);
    }


}
