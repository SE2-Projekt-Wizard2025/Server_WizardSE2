package com.aau.wizard.util;

import com.aau.wizard.model.Card;
import com.aau.wizard.model.Player;
import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.CardType;

import java.util.List;

public final class TrickRules {
    private TrickRules() {
        //wird gebraucht...
    }

    public static Player determineTrickWinner(List<Pair<Player, Card>> playedCards, CardSuit trumpCardSuit) {
        if (playedCards.isEmpty()) {
            throw new IllegalArgumentException("No cards played");
        }

        CardSuit leadCardSuit = null;
        Pair<Player, Card> firstNonJester = playedCards.stream()
                .filter(pair -> pair.second.getType() != CardType.JESTER)
                .findFirst()
                .orElse(null);

        Card firstCard = playedCards.get(0).second;
        if (firstCard.getType() == CardType.JESTER) {
            if (firstNonJester != null) {
                leadCardSuit = firstNonJester.second.getSuit();
            }
        } else if (firstCard.getType() != CardType.WIZARD) {
            leadCardSuit = firstCard.getSuit();
        }

        final CardSuit finalLeadCardSuit = leadCardSuit;
        Pair<Player, Integer> winner = playedCards.stream()
                .map(pair -> {
                    Card card = pair.second;
                    int score;
                    if (card.getType() == CardType.WIZARD) {
                        score = Integer.MAX_VALUE; // Wizard always wins
                    } else if (card.getType() == CardType.JESTER) {
                        score = Integer.MIN_VALUE; // Jester always loses
                    } else {
                        if (card.getSuit() == trumpCardSuit) {
                            score = 1000 + card.getValue();
                        } else if (card.getSuit() == finalLeadCardSuit) {
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

    public static boolean isValidPlay(Player player, Card card, List<Pair<Player, Card>> currentTrick) {
        if (card.getType() == CardType.WIZARD || card.getType() == CardType.JESTER) {
            return true;
        }
        if (currentTrick.isEmpty()) {
            return true;
        }

        CardSuit leadCardSuit = currentTrick.get(0).second.getSuit();
        boolean hasLeadSuit = player.getHandCards().stream().anyMatch(c -> c.getSuit() == leadCardSuit);
        return !hasLeadSuit || card.getSuit() == leadCardSuit;
    }
}