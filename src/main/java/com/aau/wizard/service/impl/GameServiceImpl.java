package com.aau.wizard.service.impl;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public GameServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);


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
    private GameResponse createGameResponse(Game game, String requestingPlayerId, ICard trumpCard) {
        List<PlayerDto> playerDtos = mapOrEmpty(game.getPlayers(), PlayerDto::from);
        Player requestingPlayer = game.getPlayerById(requestingPlayerId);
        List<CardDto> handCards = CardDto.safeFromPlayer(requestingPlayer);

        String currentPredictionPlayerId = null;
        if (game.getStatus() == GameStatus.PREDICTION) {
            long predictedCount = game.getPlayers().stream().filter(p -> p.getPrediction() != null).count();
            currentPredictionPlayerId = game.getPredictionOrder().get((int) predictedCount);
        }

        return new GameResponse(
                game.getGameId(),
                game.getStatus(),
                game.getCurrentPlayerId(),
                playerDtos,
                handCards,
                null,// lastPlayedCard can be set here later on
                trumpCard != null ? CardDto.from(trumpCard) : null,
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

        int numPlayers=game.getPlayers().size();
        game.setMaxRound(60/numPlayers); //Wizard regel
        game.setCurrentRound(1);

        boolean started = game.startGame(); // ruft neue Methode aus Game.java auf
        if (!started) {
            throw new IllegalStateException("Spiel konnte nicht gestartet werden – evtl. zu wenig Spieler?");
        }

        RoundServiceImpl roundService = new RoundServiceImpl(game, messagingTemplate, this);
        roundService.startRound(game.getCurrentRound());
        ICard trumpCard = roundService.trumpCard;
        // CardDto trumpCardDto = trumpCard != null ? CardDto.from(trumpCard) : null;
        roundServices.put(gameId, roundService);

        for (Player player : game.getPlayers()) {
            GameResponse response = createGameResponse(game, player.getPlayerId(), trumpCard);
            messagingTemplate.convertAndSend("/topic/game/" + player.getPlayerId(), response);
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
            throw new IllegalArgumentException("Spiel nicht gefunden");
        }

        Player player = game.getPlayerById(request.getPlayerId());
        if (player == null) {
            throw new IllegalArgumentException("Spieler nicht gefunden");
        }

        int prediction = request.getPrediction();

        List<Player> allPlayers = game.getPlayers();
        List<String> predictionOrder = game.getPredictionOrder();

        long alreadyPredicted = allPlayers.stream().filter(p -> p.getPrediction() != null).count();
        String expectedPlayerId = predictionOrder.get((int) alreadyPredicted);
        //logging
        logger.info("Vorhersage erhalten von: {}", player.getPlayerId());
        logger.info("Erwartet wird Vorhersage von: {}", expectedPlayerId);
        if (!expectedPlayerId.equals(player.getPlayerId())) {
            throw new IllegalStateException("Du bist noch nicht an der Reihe, bitte warte.");
        }

        boolean isLastPlayer = predictionOrder.indexOf(player.getPlayerId()) == predictionOrder.size() - 1;
        if (isLastPlayer) {
            int sumOfOtherPredictions = allPlayers.stream()
                    .filter(p -> !p.getPlayerId().equals(player.getPlayerId()))
                    .map(p -> p.getPrediction() != null ? p.getPrediction() : 0)
                    .reduce(0, Integer::sum);
            int totalTricks = player.getHandCards().size();
            if (sumOfOtherPredictions + prediction == totalTricks) {
                throw new IllegalArgumentException("Diese Vorhersage ergibt exakt die Anzahl der Stiche und ist damit verboten.");
            }
        }

        player.setPrediction(prediction);

        //Status auf PLAYING setzen, wenn alle vorhergesagt haben
        boolean allPredicted = allPlayers.stream().allMatch(p -> p.getPrediction() != null);
        RoundServiceImpl roundService = roundServices.get(game.getGameId());
        ICard trumpCard = roundService != null ? roundService.getTrumpCard() : null;

        if (allPredicted) {
            game.setStatus(GameStatus.PLAYING);
            game.setCurrentPlayerId(game.getPredictionOrder().get(0));
            logger.info("Vorhersagen abgeschlossen. Spiel startet. Erste SpielerIn: {}", game.getCurrentPlayerId());

            //Sende aktualisierten Zustand mit Trumpfkarte an alle Spieler
            for (Player p : game.getPlayers()) {
                GameResponse response = createGameResponse(game, p.getPlayerId(), trumpCard);
                messagingTemplate.convertAndSend("/topic/game/" + p.getPlayerId(), response);
            }
        }else{
            for (Player p : game.getPlayers()) {
                GameResponse updatedResponse = createGameResponse(game, p.getPlayerId(), trumpCard);
                messagingTemplate.convertAndSend("/topic/game/" + p.getPlayerId(), updatedResponse);
            }
        }


        //Normale Antwort (nur für den Spieler, der gerade vorhersagt)
        return createGameResponse(game, player.getPlayerId(), trumpCard);
    }

    public PlayerDto toDto(Player player) {
        PlayerDto dto = new PlayerDto();
        dto.setPlayerName(player.getName());
        dto.setPrediction(player.getPrediction() != null ? player.getPrediction() : 0);
        dto.setTricksWon(player.getTricksWon());
        dto.setScore(player.getScore());
        return dto;
    }

    public List<PlayerDto> getScoreboard(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Spiel nicht gefunden");
        }

        return game.getPlayers().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void processEndOfRound(String gameId){
        Game game = games.get(gameId);
        if(game==null || game.getStatus() != GameStatus.PLAYING){
            return;
        }

        if(game.getCurrentRound() >= game.getMaxRound()){
            game.setStatus(GameStatus.ENDED);
            logger.info("Spiel {} ist beendet.", gameId);

            for (Player player : game.getPlayers()){
                GameResponse finalResponse = createGameResponse(game, player.getPlayerId(), null);
                messagingTemplate.convertAndSend("/topic/game/" + player.getPlayerId(), finalResponse);
            }
        }else{
            game.setCurrentRound(game.getCurrentRound()+1);
            logger.info("Starte Runde {} für Spiel {}", game.getMaxRound(), gameId);

            RoundServiceImpl roundService=roundServices.get(gameId);
            roundService.startRound(game.getCurrentRound());

            for(Player player:game.getPlayers()){
                GameResponse response=createGameResponse(game, player.getPlayerId(), roundService.trumpCard);
                messagingTemplate.convertAndSend("/topic/game/" + player.getPlayerId(), response);            }
        }

    }

    @Override
    public GameResponse playCard(GameRequest request) {
        Game game = games.get(request.getGameId());
        if (game == null || game.getStatus() != GameStatus.PLAYING) {
            throw new IllegalStateException("Das Spiel ist nicht aktiv oder wurde nicht gefunden.");
        }

        Player player = game.getPlayerById(request.getPlayerId());
        if (player == null) {
            throw new IllegalArgumentException("Spieler nicht gefunden.");
        }
        //logging
        logger.info("Server erwartet: {}", game.getCurrentPlayerId());
        logger.info("Spieler sendet: {}", request.getPlayerId());
        if (!game.getCurrentPlayerId().equals(player.getPlayerId())) {
            throw new IllegalStateException("Du bist nicht an der Reihe.");
        }

        RoundServiceImpl roundService = roundServices.get(request.getGameId());
        if (roundService == null) {
            throw new IllegalStateException("Runden-Logik für dieses Spiel nicht gefunden.");
        }

        ICard cardObject = ICard.fromString(request.getCard());

        ICard cardToPlay = player.getHandCards().stream()
                .filter(c -> c.equals(cardObject))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Karte nicht in der Hand des Spielers: " + request.getCard()));

        roundService.playCard(player, cardToPlay, request.getIsCheating());

        // Prüfen, ob der Stich beendet ist
        if (roundService.getPlayedCards().size() == game.getPlayers().size()) {
            Player trickWinner = roundService.endTrick();
            game.setCurrentPlayerId(trickWinner.getPlayerId());

            for (Player p : game.getPlayers()) {
                GameResponse playerResponse = createGameResponse(game, p.getPlayerId(), roundService.getTrumpCard());
                playerResponse.setLastPlayedCard(cardToPlay.toString());
                playerResponse.setLastTrickWinnerId(trickWinner.getPlayerId());
                messagingTemplate.convertAndSend("/topic/game/" + p.getPlayerId(), playerResponse);
            }

            if (trickWinner.getHandCards().isEmpty()) {
                roundService.endRound();
            }

            GameResponse response = createGameResponse(game, request.getPlayerId(), roundService.getTrumpCard());
            response.setLastPlayedCard(cardToPlay.toString());
            return response;
        } else {
            int currentPlayerIndex = game.getPlayers().indexOf(player);
            int nextPlayerIndex = (currentPlayerIndex + 1) % game.getPlayers().size();
            game.setCurrentPlayerId(game.getPlayers().get(nextPlayerIndex).getPlayerId());

            for (Player p : game.getPlayers()) {
                GameResponse playerResponse = createGameResponse(game, p.getPlayerId(), roundService.getTrumpCard());
                playerResponse.setLastPlayedCard(cardToPlay.toString());
                messagingTemplate.convertAndSend("/topic/game/" + p.getPlayerId(), playerResponse);
            }

            GameResponse response = createGameResponse(game, request.getPlayerId(), roundService.getTrumpCard());
            response.setLastPlayedCard(cardToPlay.toString());
            return response;        }
    }
}

