package com.aau.wizard.dto.response;

import com.aau.wizard.dto.CardDto;
import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.model.enums.GameStatus;

import java.util.List;

/**
 * Response object sent from server to client containing the current game state.
 */
public class GameResponse {
    private String gameId;
    private GameStatus status;
    private String currentPlayerId;
    private List<PlayerDto> players;
    private List<CardDto> handCards;
    private String lastPlayedCard;
    private CardDto trumpCard;
    private int currentRound;
    private String currentPredictionPlayerId;

    /**
     * No args constructor needed for Jackson / JSON deserialization
     */
    public GameResponse() {}

    public GameResponse(String gameId, GameStatus status, String currentPlayerId,
                        List<PlayerDto> players, List<CardDto> handCards, String lastPlayedCard, CardDto trumpCard, int currentRound, String currentPredictionPlayerId) {
        this.gameId = gameId;
        this.status = status;
        this.currentPlayerId = currentPlayerId;
        this.players = players;
        this.handCards = handCards;
        this.lastPlayedCard = lastPlayedCard;
        this.trumpCard = trumpCard;
        this.currentRound=currentRound;
        this.currentPredictionPlayerId = currentPredictionPlayerId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public String getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayerId(String currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    public List<PlayerDto> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerDto> players) {
        this.players = players;
    }

    public String getLastPlayedCard() {
        return lastPlayedCard;
    }

    public void setLastPlayedCard(String lastPlayedCard) {
        this.lastPlayedCard = lastPlayedCard;
    }

    public List<CardDto> getHandCards() {
        return handCards;
    }

    public void setHandCards(List<CardDto> handCards) {
        this.handCards = handCards;
    }

    public CardDto getTrumpCard() {
        return trumpCard;
    }

    public void setTrumpCard(CardDto trumpCard) {
        this.trumpCard = trumpCard;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public String getCurrentPredictionPlayerId() {
        return currentPredictionPlayerId;
    }

    public void setCurrentPredictionPlayerId(String currentPredictionPlayerId) {
        this.currentPredictionPlayerId = currentPredictionPlayerId;
    }
}
