package com.aau.wizard.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player {
    private String playerId;
    private String name;
    private int score;
    private int bid;
    private int tricksWon;
    private boolean ready;
    private List<Card> handCards;

    public Player(String playerId, String name) {
        this.playerId = playerId;
        this.name = name;
        this.ready = false;
        this.handCards = new ArrayList<>();
        this.bid = 0;
        this.tricksWon = 0;
        this.score = 0;
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

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getTricksWon() {
        return tricksWon;
    }

    public void setTricksWon(int tricksWon) {
        this.tricksWon = tricksWon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player that = (Player) o;
        return bid == that.bid &&
                tricksWon == that.tricksWon &&
                score == that.score &&
                Objects.equals(playerId, that.playerId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(handCards, that.handCards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, name, handCards, bid, tricksWon, score);
    }

    @Override
    public String toString() {
        return "PlayerStateK{" +
                "playerId='" + playerId + '\'' +
                ", name='" + name + '\'' +
                ", hand=" + handCards +
                ", bid=" + bid +
                ", tricksWon=" + tricksWon +
                ", score=" + score +
                '}';
    }
}
