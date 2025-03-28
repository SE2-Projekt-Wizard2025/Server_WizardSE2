package com.aau.wizard.model;

import java.util.UUID;

public class Game {
    // TODO: Add further attributes / getters / setters

    private final String gameId;

    public Game() {
        this.gameId = UUID.randomUUID().toString();
    }

    // TODO: Constructor with additional attributes

    public String getGameId() {
        return gameId;
    }
}
