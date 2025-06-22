package com.aau.wizard.util;

import com.aau.wizard.model.ICard;
import com.aau.wizard.model.Player;
import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.CardType;

import java.util.Comparator;
import java.util.List;

public final class TrickRules {
    private TrickRules() {
        //wird gebraucht...
    }

    public static Player determineTrickWinner(List<Pair<Player, ICard>> playedCards, CardSuit trumpCardSuit) {
        if (playedCards.isEmpty()) {
            throw new IllegalArgumentException("No cards played");
        }

        CardSuit leadCardSuit = null;
        Pair<Player, ICard> firstNonJester = playedCards.stream()
                .filter(pair -> pair.second.getType() != CardType.JESTER)
                .findFirst()
                .orElse(null);

        ICard firstCard = playedCards.get(0).second;
        if (firstCard.getType() == CardType.JESTER) {
            if (firstNonJester != null) {
                leadCardSuit = firstNonJester.second.getSuit();
            }
        } else if (firstCard.getType() != CardType.WIZARD) {
            leadCardSuit = firstCard.getSuit();
        }

        final CardSuit finalLeadCardSuit = leadCardSuit;
        List<TrickCardInfo> cardScores= new java.util.ArrayList<>();
        for (int i=0; i< playedCards.size(); i++){
            Pair<Player, ICard> pair=playedCards.get(i);
                    ICard card = pair.second;
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
                    cardScores.add(new TrickCardInfo(pair.first, score, i));
                }

        TrickCardInfo winnerInfo = cardScores.stream()
                .max(Comparator
                        .<TrickCardInfo>comparingInt(info -> info.score) // Höchster Score gewinnt
                        .thenComparingInt(info -> info.originalIndex))   // Bei gleichem Score, frühere Karte gewinnt
                .orElseThrow(() -> new IllegalStateException("No winner determined"));

        return winnerInfo.player;
    }

    public static boolean isValidPlay(Player player, ICard card, List<Pair<Player, ICard>> currentTrick, boolean isCheating) {
        if (isCheating) {
            return true;
        }

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

    private static class TrickCardInfo{
        Player player;
        int score;
        int originalIndex; //index der gespielten Karte im Stich

        TrickCardInfo(Player player, int score, int originalIndex){
            this.player=player;
            this.score=score;
            this.originalIndex=originalIndex;
        }
    }
}