package com.aau.wizard.service.impl;

import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.model.ICard;
import com.aau.wizard.model.Deck;
import com.aau.wizard.model.Game;
import com.aau.wizard.model.Player;
import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.GameStatus;
import com.aau.wizard.service.interfaces.GameService;
import com.aau.wizard.util.BiddingRules;
import com.aau.wizard.util.Pair;
import com.aau.wizard.util.TrickRules;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RoundServiceImpl {

    final List<Player> players;
    public Deck deck;
    public ICard trumpCard = null;
    public CardSuit trumpCardSuit = null;
    public final List<Pair<Player, ICard>> playedCards = new ArrayList<>();
    public int currentTrickNumber = 0;
    private final Game game;
    private final SimpMessagingTemplate messagingTemplate;
    private final GameService gameService;
    private static final Logger logger = LoggerFactory.getLogger(RoundServiceImpl.class);


    public RoundServiceImpl(Game game, SimpMessagingTemplate messagingTemplate, GameService gameService) {
        this.players = game.getPlayers();
        this.game=game;
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
    }

    public void startRound(int roundNumber) {
        this.deck = new Deck();
        deck.shuffle();

        game.setStatus(GameStatus.PREDICTION);
        game.setPredictionOrder(createPredictionOrder(players, game.getCurrentPlayerId()));

        for (Player player : players) {
            List<ICard> hand = new ArrayList<>(deck.draw(roundNumber));
            player.setHandCards(hand);
            player.setTricksWon(0);
            player.setBid(0);
            player.setPrediction(null);
        }

        if (deck.size() < 1) {
            trumpCard = null;
            trumpCardSuit = null;
        } else {
            List<ICard> drawn = deck.draw(1);
            trumpCard = drawn.isEmpty() ? null : drawn.get(0);
            trumpCardSuit = trumpCard != null ? trumpCard.getSuit() : null;
        }

        currentTrickNumber = 0;
        playedCards.clear();
    }

    public void playCard(Player player, ICard card, boolean isCheating) {
        if (!player.getHandCards().contains(card)) {
            throw new IllegalArgumentException("Player doesn't have that card");
        }
        synchronized (playedCards) {

            if (!playedCards.isEmpty() && !TrickRules.isValidPlay(player, card, playedCards, trumpCardSuit, isCheating)) {
                throw new IllegalStateException("Invalid card play: " + card + " by " + player.getName());
            }

            player.getHandCards().remove(card);
            playedCards.add(new Pair<>(player, card));
        }
    }

    public Player endTrick() {
        if (playedCards.isEmpty()) {
            throw new IllegalStateException("No cards played in this trick");
        }

        Player winner = TrickRules.determineTrickWinner(playedCards, trumpCardSuit);
        winner.setTricksWon(winner.getTricksWon() + 1);

        playedCards.clear();
        currentTrickNumber++;
        return winner;
    }

    private List<String> createPredictionOrder(List<Player> players, String startingPlayerId) {
        List<String> order = new ArrayList<>();
        int startIndex = -1;

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getPlayerId().equals(startingPlayerId)) {
                startIndex = i;
                break;
            }
        }

        if (startIndex == -1) {
          if (players.isEmpty()) {
                throw new IllegalArgumentException("Keine Spieler im Spiel.");
            }
            startIndex = 0;
        }

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get((startIndex + i) % players.size());
            order.add(p.getPlayerId());
        }

        return order;
    }


    public void endRound() {

        String gameId = game.getGameId();
        Map<String, Integer> scoresBeforeRound = new HashMap<>();
        for (Player player : players) {
            scoresBeforeRound.put(player.getPlayerId(), player.getScore());
        }
        BiddingRules.calculateScores(players);

        for (Player player : players) {
            int pointsThisRound = player.getScore() - scoresBeforeRound.get(player.getPlayerId());
            player.addRoundScore(pointsThisRound);
        }

        Player winner = players.stream()
                .max((p1, p2) -> Integer.compare(p1.getTricksWon(), p2.getTricksWon()))
                .orElseThrow(() -> new IllegalStateException("Kein Gewinner gefunden"));

        List<String> predictionOrder = createPredictionOrder(players, winner.getPlayerId());
        game.setPredictionOrder(predictionOrder);

        game.setStatus(GameStatus.ROUND_END_SUMMARY);

        messagingTemplate.convertAndSend(
                "/topic/game/" + gameId + "/scoreboard",
                gameService.getScoreboard(gameId)
        );

        for (Player player : players) {
            GameResponse response = gameService.createGameResponse(game, player.getPlayerId(), trumpCard);
            messagingTemplate.convertAndSend("/topic/game/" + player.getPlayerId(), response);
        }
    }

    public List<Pair<Player, ICard>> getPlayedCards() {
        return playedCards;
    }

    public ICard getTrumpCard() {
        return trumpCard;
    }

    public void proceedToNextRound(String gameId) {
        gameService.processEndOfRound(gameId);
    }
}