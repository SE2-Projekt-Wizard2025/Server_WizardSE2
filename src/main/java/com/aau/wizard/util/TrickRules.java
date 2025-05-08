package com.aau.wizard.util;

import com.aau.wizard.model.Card;
import com.aau.wizard.model.CardType;
import com.aau.wizard.model.Suit;
import com.aau.wizard.model.PlayerState;
import com.aau.wizard.util.Pair;
import java.util.List;
import java.util.stream.Collectors;

public final class TrickRules {
    private TrickRules() {
        // Private constructor to prevent instantiation
    }

    public static PlayerState determineTrickWinner(List<Pair<PlayerState, Card>> playedCards, Suit trumpSuit) {
        if (playedCards.isEmpty()) {
            throw new IllegalArgumentException("No cards played");
        }

        Suit leadSuit = null;
        Pair<PlayerState, Card> firstNonJester = playedCards.stream()
                .filter(pair -> pair.second.getType() != CardType.JESTER)
                .findFirst()
                .orElse(null);

        Card firstCard = playedCards.get(0).second;
        if (firstCard.getType() == CardType.JESTER) {
            if (firstNonJester != null) {
                leadSuit = firstNonJester.second.getSuit();
            }
        } else if (firstCard.getType() != CardType.WIZARD) {
            leadSuit = firstCard.getSuit();
        }

        final Suit finalLeadSuit = leadSuit;
        Pair<PlayerState, Integer> winner = playedCards.stream()
                .map(pair -> {
                    Card card = pair.second;
                    int score;
                    if (card.getType() == CardType.WIZARD) {
                        score = Integer.MAX_VALUE; // Wizard always wins
                    } else if (card.getType() == CardType.JESTER) {
                        score = Integer.MIN_VALUE; // Jester always loses
                    } else {
                        if (card.getSuit() == trumpSuit) {
                            score = 1000 + card.getValue();
                        } else if (card.getSuit() == finalLeadSuit) {
                            score = 100 + card.getValue();
                        } else {
                            score = 0;
                        }
                    }
                    return new Pair<>(pair.first, score);
                })
                .max((p1, p2) -> Integer.compare(p1.second, p2.second))
                .orElseThrow(() -> new IllegalStateException("No winner determined"));

        return winner.first;
    }

    public static boolean isValidPlay(PlayerState player, Card card, List<Pair<PlayerState, Card>> currentTrick) {
        if (card.getType() == CardType.WIZARD || card.getType() == CardType.JESTER) {
            return true; // these can always be played
        }
        if (currentTrick.isEmpty()) {
            return true; // when playing first
        }

        Suit leadSuit = currentTrick.get(0).second.getSuit();
        boolean hasLeadSuit = player.getHand().stream().anyMatch(c -> c.getSuit() == leadSuit);
        return !hasLeadSuit || card.getSuit() == leadSuit; // doesn't have the card, or has chosen matching color
    }
}