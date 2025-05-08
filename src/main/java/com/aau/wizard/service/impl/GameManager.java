package com.aau.wizard.service.impl;

import com.aau.wizard.model.Card;
import com.aau.wizard.model.CardType;
import com.aau.wizard.model.Deck;
import com.aau.wizard.model.Suit;
import com.aau.wizard.model.PlayerState;
import com.aau.wizard.util.Pair;
import com.aau.wizard.util.TrickRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameManager {

    final List<PlayerState> players;
    final Deck deck = new Deck();
    Card trumpCard = null;
    Suit trumpSuit = null;
    final List<Pair<PlayerState, Card>> playedCards = new ArrayList<>();
    int currentTrickNumber = 0;

    public GameManager(List<PlayerState> players) {
        this.players = players;
    }

    public void startRound(int roundNumber) {
        deck.shuffle();
        for (PlayerState player : players) {
            player.setHand(new ArrayList<>(deck.draw(roundNumber)));
            player.setTricksWon(0);
            player.setBid(0);
        }

        if (deck.size() < 1) {
            trumpCard = null;
            trumpSuit = null;
        } else {
            List<Card> drawn = deck.draw(1);
            trumpCard = drawn.isEmpty() ? null : drawn.get(0);
            trumpSuit = trumpCard != null ? trumpCard.getSuit() : null;
        }

        currentTrickNumber = 0;
        playedCards.clear();

        System.out.println("Trumpf: " + (trumpSuit != null ? trumpSuit : "Kein Trumpf"));
    }

    public void playCard(PlayerState player, Card card) {
        if (!player.getHand().contains(card)) {
            throw new IllegalArgumentException("Player doesn't have that card");
        }

        if (!playedCards.isEmpty() && !TrickRules.isValidPlay(player, card, playedCards)) {
            throw new IllegalStateException("Invalid card play: " + card + " by " + player.getName());
        }

        player.getHand().remove(card);
        playedCards.add(new Pair<>(player, card));
    }

    public PlayerState endTrick() {
        if (playedCards.isEmpty()) {
            throw new IllegalStateException("No cards played in this trick");
        }

        PlayerState winner = TrickRules.determineTrickWinner(playedCards, trumpSuit);
        winner.setTricksWon(winner.getTricksWon() + 1);
        System.out.println("Stich " + (currentTrickNumber + 1) + " gewonnen von " + winner.getName() + " (" + winner.getTricksWon() + " Stiche)");

        playedCards.clear();
        currentTrickNumber++;
        return winner;
    }

    public void endRound() {
        for (PlayerState player : players) {
            int difference = Math.abs(player.getTricksWon() - player.getBid());
            int score;
            if (difference == 0) {
                score = 20 + (player.getTricksWon() * 10);
            } else {
                score = (player.getTricksWon() * 10) - (difference * 10);
            }
            player.setScore(player.getScore() + score);
        }

        System.out.println("\n=== Finale Auswertung ===");
        for (PlayerState player : players) {
            System.out.println(player.getName() + ": " + player.getBid() + " geboten, " +
                    player.getTricksWon() + " gewonnen → Punkte: " + player.getScore());
        }
    }
}
