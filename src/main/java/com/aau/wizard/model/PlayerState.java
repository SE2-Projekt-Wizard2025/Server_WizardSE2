package com.aau.wizard.model;

import com.aau.wizard.model.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerState {

    private final String playerId;
    private final String name;
    private List<Card> hand;
    private int bid;
    private int tricksWon;
    private int score;

    public PlayerState(String playerId, String name) {
        this.playerId = playerId;
        this.name = name;
        this.hand = new ArrayList<>();
        this.bid = 0;
        this.tricksWon = 0;
        this.score = 0;
    }

    public PlayerState(String playerId, String name, List<Card> hand, int bid, int tricksWon, int score) {
        this.playerId = playerId;
        this.name = name;
        this.hand = hand;
        this.bid = bid;
        this.tricksWon = tricksWon;
        this.score = score;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerState)) return false;
        PlayerState that = (PlayerState) o;
        return bid == that.bid &&
                tricksWon == that.tricksWon &&
                score == that.score &&
                Objects.equals(playerId, that.playerId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(hand, that.hand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, name, hand, bid, tricksWon, score);
    }

    @Override
    public String toString() {
        return "PlayerStateK{" +
                "playerId='" + playerId + '\'' +
                ", name='" + name + '\'' +
                ", hand=" + hand +
                ", bid=" + bid +
                ", tricksWon=" + tricksWon +
                ", score=" + score +
                '}';
    }
}
