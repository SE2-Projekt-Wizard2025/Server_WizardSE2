package com.aau.wizard.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String playerId;
    private String name;
    private int score;
    private List<Integer> roundScores = new ArrayList<>(); // Punkte der Runde
    private boolean ready;
    private List<ICard> handCards = new ArrayList<>();
    private Integer prediction; //kann null sein, noch keine Vorhersage
    private int tricksWon;
    private int bid;

    public Player(String playerId, String name) {
        this.playerId = playerId;
        this.name = name;
        this.ready = false;
        this.score = 0;
        this.tricksWon = 0;
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

    public List<ICard> getHandCards() {
        return handCards;
    }

    public void setHandCards(List<ICard> handCards) {
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

    public Integer getPrediction(){return prediction;}

    public void setPrediction(Integer prediction){this.prediction=prediction;}

    public int getBid(){
        return bid;
    }

    public void setBid(int bid){
        this.bid=bid;
    }

    public int getTricksWon(){
        return tricksWon;
    }

    public void setTricksWon(int tricksWon){
        this.tricksWon=tricksWon;
    }

    public List<Integer> getRoundScores() {
        return roundScores;
    }

    public void addRoundScore(int scoreThisRound) {
        this.roundScores.add(scoreThisRound);
    }
}
