package com.aau.wizard.service.impl;
import com.aau.wizard.GameExceptions;
import com.aau.wizard.dto.CardDto;
import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.request.PredictionRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.model.Game;
import com.aau.wizard.model.ICard;
import com.aau.wizard.model.Player;
import com.aau.wizard.model.enums.GameStatus;
import com.aau.wizard.service.interfaces.GameService;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.aau.wizard.util.CollectionUtils.mapOrEmpty;
import com.aau.wizard.GameExceptions.CardNotInHandException;
import com.aau.wizard.GameExceptions.GameAlreadyEndedException;
import com.aau.wizard.GameExceptions.GameNotActiveException;
import com.aau.wizard.GameExceptions.GameNotFoundException;
import com.aau.wizard.GameExceptions.GameStartException;
import com.aau.wizard.GameExceptions.InvalidPredictionException;
import com.aau.wizard.GameExceptions.InvalidTurnException;
import com.aau.wizard.GameExceptions.PlayerNotFoundException;
import com.aau.wizard.GameExceptions.RoundLogicException;
import com.aau.wizard.GameExceptions.RoundProgressionException;

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

    private final SimpMessagingTemplate messagingTemplate;

    private static final String GAME_TOPIC_PREFIX = "/topic/game/";

    public GameServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

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

    @Override
    public GameResponse createGameResponse(Game game, String requestingPlayerId, ICard trumpCard) {
        List<PlayerDto> playerDtos = mapOrEmpty(game.getPlayers(), PlayerDto::from);
        Player requestingPlayer = game.getPlayerById(requestingPlayerId);
        List<CardDto> handCards = CardDto.safeFromPlayer(requestingPlayer);

        String currentPredictionPlayerId = null;
        if (game.getStatus() == GameStatus.PREDICTION) {
            long predictedCount = game.getPlayers().stream().filter(p -> p.getPrediction() != null).count();
            if (!game.getPredictionOrder().isEmpty() && (int)predictedCount < game.getPredictionOrder().size()) {
                currentPredictionPlayerId = game.getPredictionOrder().get((int) predictedCount);
            }
        }
        CardDto trumpCardDto = trumpCard != null ? CardDto.from(trumpCard) : null;

        return new GameResponse(
                game.getGameId(),
                game.getStatus(),
                game.getCurrentPlayerId(),
                playerDtos,
                handCards,
                null,// lastPlayedCard can be set here later on
                trumpCardDto,
                game.getCurrentRound(),
                currentPredictionPlayerId
        );
    }

    /**
     * Adds a new player to the game if they are not already part of it.
     *
     * @param game the game the player wants to join
     * @param request the request containing playerId and playerName
     */
    private void addPlayerIfAbsent(Game game, GameRequest request) {
        if (game.getPlayerById(request.getPlayerId()) == null) {
            game.getPlayers().add(new Player(request.getPlayerId(), request.getPlayerName()));
        }
    }

    /**
     * Checks whether the player is not yet part of the game.
     * Used to avoid duplicate joins.
     */

    @Override
    public GameResponse startGame(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new GameNotFoundException("Spiel nicht gefunden: " + gameId);
        }

        int numPlayers=game.getPlayers().size();
        game.setMaxRound(60/numPlayers); //Wizard regel
        game.setCurrentRound(1);

        if (!game.startGame()) {
            throw new GameStartException("Spiel konnte nicht gestartet werden – evtl. zu wenig Spieler?");
        }

        RoundServiceImpl roundService = new RoundServiceImpl(game, messagingTemplate, this);
        roundService.startRound(game.getCurrentRound());
        ICard trumpCard = roundService.trumpCard;
        roundServices.put(gameId, roundService);

        for (Player player : game.getPlayers()) {
            GameResponse response = createGameResponse(game, player.getPlayerId(), trumpCard);
            messagingTemplate.convertAndSend(GAME_TOPIC_PREFIX + player.getPlayerId(), response);
        }


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
            throw new GameNotFoundException("Spiel nicht gefunden");
        }

        Player player = game.getPlayerById(request.getPlayerId());
        if (player == null) {
            throw new PlayerNotFoundException("Spieler nicht gefunden");
        }

        validatePredictionTurn(game, player);

        validateLastPlayerPrediction(game, player, request.getPrediction());

        player.setPrediction(request.getPrediction());

        boolean allPredicted = game.getPlayers().stream().allMatch(p -> p.getPrediction() != null);
        RoundServiceImpl roundService = roundServices.get(game.getGameId());
        ICard trumpCard = roundService != null ? roundService.getTrumpCard() : null;

        if (allPredicted) {
            game.setStatus(GameStatus.PLAYING);
            game.setCurrentPlayerId(game.getPredictionOrder().get(0));

            notifyAllPlayersOfGameUpdate(game, trumpCard);
        }
        notifyAllPlayersOfGameUpdate(game, trumpCard);
        return createGameResponse(game, player.getPlayerId(), trumpCard);
    }
    private void validatePredictionTurn(Game game, Player player) {
        long alreadyPredicted = game.getPlayers().stream().filter(p -> p.getPrediction() != null).count();
        String expectedPlayerId = game.getPredictionOrder().get((int) alreadyPredicted);
        if (!expectedPlayerId.equals(player.getPlayerId())) {
            throw new InvalidTurnException("Du bist noch nicht an der Reihe, bitte warte.");
        }
    }

    private void validateLastPlayerPrediction(Game game, Player player, int prediction) {
        boolean isLastPlayer = game.getPredictionOrder().indexOf(player.getPlayerId()) == game.getPredictionOrder().size() - 1;
        if (isLastPlayer) {
            int sumOfOtherPredictions = game.getPlayers().stream()
                    .filter(p -> !p.getPlayerId().equals(player.getPlayerId()))
                    .map(p -> p.getPrediction() != null ? p.getPrediction() : 0)
                    .reduce(0, Integer::sum);
            int totalTricks = player.getHandCards().size();
            if (sumOfOtherPredictions + prediction == totalTricks) {
                throw new InvalidPredictionException("Diese Vorhersage ergibt exakt die Anzahl der Stiche und ist damit verboten.");
            }
        }
    }

    private void notifyAllPlayersOfGameUpdate(Game game, ICard trumpCard) {
        for (Player p : game.getPlayers()) {
            GameResponse response = createGameResponse(game, p.getPlayerId(), trumpCard);

            messagingTemplate.convertAndSend(GAME_TOPIC_PREFIX + p.getPlayerId(), response);
        }
    }

    public PlayerDto toDto(Player player) {
        PlayerDto dto = new PlayerDto();
        dto.setPlayerId(player.getPlayerId());
        dto.setPlayerName(player.getName());
        dto.setPrediction(player.getPrediction() != null ? player.getPrediction() : 0);
        dto.setTricksWon(player.getTricksWon());
        dto.setScore(player.getScore());
        dto.setRoundScores(player.getRoundScores());
        return dto;
    }

    public List<PlayerDto> getScoreboard(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new GameNotFoundException("Spiel nicht gefunden");
         }

        return game.getPlayers().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void processEndOfRound(String gameId){
        Game game = games.get(gameId);
        if (game == null) {
            return;
        }
        if (game.getStatus() == GameStatus.ENDED) return;

        if(game.getCurrentRound() >= game.getMaxRound()){
            game.setStatus(GameStatus.ENDED);

            for (Player player : game.getPlayers()){
                GameResponse finalResponse = createGameResponse(game, player.getPlayerId(), null);
                messagingTemplate.convertAndSend(GAME_TOPIC_PREFIX + player.getPlayerId(), finalResponse);
            }
        }else {
            try {
                game.setCurrentRound(game.getCurrentRound() + 1);

                RoundServiceImpl roundService = roundServices.get(gameId);
                if (roundService == null) {
                   throw new RoundLogicException("RoundService for game " + gameId + " not found during end of round processing.");
                }
                roundService.startRound(game.getCurrentRound());

                for (Player player : game.getPlayers()) {
                    GameResponse response = createGameResponse(game, player.getPlayerId(), roundService.trumpCard);
                    messagingTemplate.convertAndSend(GAME_TOPIC_PREFIX + player.getPlayerId(), response);
                }
            } catch (Exception e) {
                throw new RoundProgressionException("Fehler beim Start der nächsten Runde in processEndOfRound", e);
            }
        }
    }
    @Override
    public void proceedToNextRound(String gameId) {
        processEndOfRound(gameId);
    }

    @Override
    public GameResponse playCard(GameRequest request) {
        Game game = games.get(request.getGameId());

        boolean isCheating = Boolean.TRUE.equals(request.getIsCheating());

        if (game == null) {
            throw new GameNotFoundException("Spiel nicht gefunden");
        }

        if (game.getStatus() == GameStatus.ENDED) {
            throw new GameAlreadyEndedException("Das Spiel ist bereits beendet.");
        }

        if (game.getStatus() != GameStatus.PLAYING) {
            throw new GameNotActiveException("Das Spiel ist nicht aktiv oder wurde nicht gefunden.");
        }

        Player player = getPlayerOrThrow(game, request.getPlayerId());
        validatePlayerTurn(game, player);

        RoundServiceImpl roundService = getRoundServiceOrThrow(request.getGameId());
        ICard cardToPlay = resolveCardToPlay(player, request.getCard());


        roundService.playCard(player, cardToPlay, isCheating);
        return handlePostPlay(game, roundService, player, cardToPlay);
    }

    private Game getActiveGameOrThrow(String gameId) {
        Game game = games.get(gameId);
        if (game == null || game.getStatus() != GameStatus.PLAYING) {
            throw new GameNotActiveException("Das Spiel ist nicht aktiv oder wurde nicht gefunden.");
        }
        return game;
    }

    private Player getPlayerOrThrow(Game game, String playerId) {
        Player player = game.getPlayerById(playerId);
        if (player == null) {
            throw new PlayerNotFoundException("Spieler nicht gefunden.");
        }
        return player;
    }

    private void validatePlayerTurn(Game game, Player player) {
        String currentPlayerId = game.getCurrentPlayerId();
        if (currentPlayerId == null || !currentPlayerId.equals(player.getPlayerId())) {
            throw new InvalidTurnException("Du bist nicht an der Reihe.");
        }
    }

    private RoundServiceImpl getRoundServiceOrThrow(String gameId) {
        RoundServiceImpl roundService = roundServices.get(gameId);
        if (roundService == null) {
            throw new RoundLogicException("Runden-Logik für dieses Spiel nicht gefunden.");
        }
        return roundService;
    }

    private ICard resolveCardToPlay(Player player, String cardStr) {
        ICard cardObject = ICard.fromString(cardStr);
        if (!player.getHandCards().contains(cardObject)) {
            throw new CardNotInHandException("Die Karte ist nicht in deiner Hand.");
        }
        return cardObject;
    }

    private GameResponse handlePostPlay(Game game, RoundServiceImpl roundService, Player player, ICard cardToPlay) {
        if (roundService.getPlayedCards().size() == game.getPlayers().size()) {
            return handleEndOfTrick(game, roundService, cardToPlay);
        } else {
            return handleNextPlayer(game, roundService, player, cardToPlay);
        }
    }

    private GameResponse handleEndOfTrick(Game game, RoundServiceImpl roundService, ICard cardToPlay) {
        Player trickWinner = roundService.endTrick();
        game.setCurrentPlayerId(trickWinner.getPlayerId());

        for (Player p : game.getPlayers()) {
            GameResponse response = createGameResponse(game, p.getPlayerId(), roundService.getTrumpCard());
            response.setLastPlayedCard(cardToPlay.toString());
            response.setLastTrickWinnerId(trickWinner.getPlayerId());
            messagingTemplate.convertAndSend(GAME_TOPIC_PREFIX + p.getPlayerId(), response);
        }

        if (trickWinner.getHandCards().isEmpty()) {
            roundService.endRound();
            return null;
        }

        GameResponse response = createGameResponse(game, trickWinner.getPlayerId(), roundService.getTrumpCard());
        response.setLastPlayedCard(cardToPlay.toString());
        response.setLastTrickWinnerId(trickWinner.getPlayerId());
        return response;
    }

    private GameResponse handleNextPlayer(Game game, RoundServiceImpl roundService, Player currentPlayer, ICard cardToPlay) {
        int currentPlayerIndex = game.getPlayers().indexOf(currentPlayer);
        int nextPlayerIndex = (currentPlayerIndex + 1) % game.getPlayers().size();
        game.setCurrentPlayerId(game.getPlayers().get(nextPlayerIndex).getPlayerId());

        for (Player p : game.getPlayers()) {
            GameResponse response = createGameResponse(game, p.getPlayerId(), roundService.getTrumpCard());
            response.setLastPlayedCard(cardToPlay.toString());
            messagingTemplate.convertAndSend(GAME_TOPIC_PREFIX + p.getPlayerId(), response);
        }

        GameResponse response = createGameResponse(game, currentPlayer.getPlayerId(), roundService.getTrumpCard());
        response.setLastPlayedCard(cardToPlay.toString());
        return response;
    }

    @Override
    public void abortGame(String gameId) {
        Game game = getGameById(gameId);

        if (game == null) {
            throw new GameExceptions.GameNotFoundException("Spiel mit ID " + gameId + " für Abbruch nicht gefunden.");
        }

        game.setStatus(GameStatus.ENDED);
        GameResponse finalResponse = createGameResponse(game, null, null);

        for (Player player : game.getPlayers()) {
            messagingTemplate.convertAndSend(GAME_TOPIC_PREFIX + player.getPlayerId(), finalResponse);
        }
    }

    @Override
    public void signalReturnToLobby(String gameId) {
        Game game = getGameById(gameId);
        if (game == null) {
            return;
        }
        messagingTemplate.convertAndSend(GAME_TOPIC_PREFIX + gameId + "/lobby", "RETURN");
    }
}

