package com.aau.wizard.service.impl;

import com.aau.wizard.model.Card;
import com.aau.wizard.model.Deck;
import com.aau.wizard.model.Player;
import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.util.Pair;
import com.aau.wizard.util.TrickRules;

import java.util.ArrayList;
import java.util.List;

public class RoundServiceImpl {

    final List<Player> players;
    public final Deck deck = new Deck();
    public Card trumpCard = null;
    public CardSuit trumpCardSuit = null;
    public final List<Pair<Player, Card>> playedCards = new ArrayList<>();
    public int currentTrickNumber = 0;

    public RoundServiceImpl(List<Player> players) {
        this.players = players;
    }

    public void startRound(int roundNumber) {
        deck.shuffle();
        for (Player player : players) {
            player.setHandCards(new ArrayList<>(deck.draw(roundNumber)));
            player.setTricksWon(0);
            player.setBid(0);
        }

        if (deck.size() < 1) {
            trumpCard = null;
            trumpCardSuit = null;
        } else {
            List<Card> drawn = deck.draw(1);
            trumpCard = drawn.isEmpty() ? null : drawn.get(0);
            trumpCardSuit = trumpCard != null ? trumpCard.getSuit() : null;
        }

        currentTrickNumber = 0;
        playedCards.clear();

        System.out.println("Trumpf: " + (trumpCardSuit != null ? trumpCardSuit : "Kein Trumpf"));
    }

    public void playCard(Player player, Card card) {
        if (!player.getHandCards().contains(card)) {
            throw new IllegalArgumentException("Player doesn't have that card");
        }

        if (!playedCards.isEmpty() && !TrickRules.isValidPlay(player, card, playedCards)) {
            throw new IllegalStateException("Invalid card play: " + card + " by " + player.getName());
        }

        player.getHandCards().remove(card);
        playedCards.add(new Pair<>(player, card));
    }

    public Player endTrick() {
        if (playedCards.isEmpty()) {
            throw new IllegalStateException("No cards played in this trick");
        }

        Player winner = TrickRules.determineTrickWinner(playedCards, trumpCardSuit);
        winner.setTricksWon(winner.getTricksWon() + 1);
        System.out.println("Stich " + (currentTrickNumber + 1) + " gewonnen von " + winner.getName() + " (" + winner.getTricksWon() + " Stiche)");

        playedCards.clear();
        currentTrickNumber++;
        return winner;
    }

    public void endRound() {
        for (Player player : players) {
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
        for (Player player : players) {
            System.out.println(player.getName() + ": " + player.getBid() + " geboten, " +
                    player.getTricksWon() + " gewonnen → Punkte: " + player.getScore());
        }
    }
}
