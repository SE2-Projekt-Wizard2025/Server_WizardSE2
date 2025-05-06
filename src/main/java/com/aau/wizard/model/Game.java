package com.aau.wizard.model;

import com.aau.wizard.model.enums.GameStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private String gameId;
    private List<Player> players = new ArrayList<>();
    private String currentPlayerId;
    private GameStatus status;

    public Game(String gameId) {
        this.gameId = gameId;
        this.status = GameStatus.LOBBY;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public String getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayerId(String currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    /**
     * Returns the {@link Player} object with the given playerId from the list of players.
     *
     * @param playerId the ID of the player to search for
     * @return the {@link Player} with the given playerId, or {@code null} if no such player exists
     */
    public Player getPlayerById(String playerId) {
        for (Player player : players) {
            if (player.getPlayerId().equals(playerId)) {
                return player;
            }
        }
        return null;
    }
    public boolean addPlayer(Player player) {
        if (status != GameStatus.LOBBY || players.size() >= 6) return false;
        players.add(player);
        return true;
    }
    //prüfen ob Spiel schon gestartet werden kann (mind. 3 Spieler)
    public boolean canStartGame() {
        return status == GameStatus.LOBBY && players.size() >= 3;
    }

    public boolean startGame() {
        if (!canStartGame()) return false;

        Collections.shuffle(players);
        status = GameStatus.PLAYING;
        currentPlayerId = players.get(0).getPlayerId();
        return true;
    }


}
