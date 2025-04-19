package com.aau.wizard.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String playerId;
    private String name;
    private int score;
    private boolean ready;
    private List<Card> handCards = new ArrayList<>();

    public Player(String playerId, String name) {
        this.playerId = playerId;
        this.name = name;
        this.ready = false;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Card> getHandCards() {
        return handCards;
    }

    public void setHandCards(List<Card> handCards) {
        this.handCards = handCards;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
