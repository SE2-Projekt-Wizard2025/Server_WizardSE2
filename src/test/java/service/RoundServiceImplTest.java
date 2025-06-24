package service;

import com.aau.wizard.model.*;
import com.aau.wizard.model.enums.CardSuit;
import com.aau.wizard.model.enums.GameStatus;
import com.aau.wizard.service.impl.RoundServiceImpl;
import com.aau.wizard.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.aau.wizard.service.interfaces.GameService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoundServiceImplTest {

    private List<Player> players;
    private RoundServiceImpl roundService;
    private Game game;
    private SimpMessagingTemplate messagingTemplate;
    private GameService gameService;


    @BeforeEach
    void setUp() {
        players = List.of(
                new Player("p1", "Alice"),
                new Player("p2", "Bob"),
                new Player("p3", "Charlie")
            );

            game = new Game("test-game");
        game.getPlayers().addAll(players);

        messagingTemplate = mock(SimpMessagingTemplate.class);
        gameService = mock(GameService.class);

        roundService = new RoundServiceImpl(game, messagingTemplate, gameService);
        }

    void prepareGameAndStartRound(int roundNumber, String startingPlayerId) {
        game.setCurrentPlayerId(startingPlayerId);
        roundService.startRound(roundNumber);
    }

    @Test
    void startRound_initializesGameStateCorrectly() {
        prepareGameAndStartRound(3, "p1");

        // Verify each player got 3 cards
        players.forEach(p -> assertEquals(3, p.getHandCards().size()));

        // Verify game state reset
        assertEquals(0, roundService.currentTrickNumber);
        assertTrue(roundService.playedCards.isEmpty());
    }

    @Test
    void startRound_setsTrumpCardWhenDeckHasCardsAfterDealing() {
        Game games = new Game("123");
        games.addPlayer(new Player("Player1", "Alice"));
        games.addPlayer(new Player("Player2", "Bob"));

        SimpMessagingTemplate messagingTemplateMock = mock(SimpMessagingTemplate.class);
        GameService gameServiceMock = mock(GameService.class);

        RoundServiceImpl roundServiceImp = new RoundServiceImpl(games, messagingTemplateMock, gameServiceMock);


        //Runde starten → deck wird hier initialisiert
        games.setCurrentPlayerId("Player1");
        roundServiceImp.startRound(3);


        assertNotNull(roundServiceImp.trumpCard, "Trumpfkarte sollte gesetzt sein");
        assertNotNull(roundServiceImp.trumpCardSuit, "Trumpffarbe sollte gesetzt sein");
    }

        @Test
    void playCard_validCard_playsSuccessfully() {
        prepareGameAndStartRound(3, "p1");
        Player player = players.get(0);
        ICard card = player.getHandCards().get(0);

        roundService.playCard(player, card, false);

        assertEquals(1, roundService.playedCards.size());
        assertEquals(2, player.getHandCards().size());
    }

    @Test
    void playCard_invalidCard_throwsException() {
        prepareGameAndStartRound(3, "p1");
        Player player = players.get(0);
        player.getHandCards().clear();

        player.getHandCards().addAll(List.of(
                CardFactory.createCard(CardSuit.RED, 1),
                CardFactory.createCard(CardSuit.GREEN, 2)
        ));

        ICard invalidCard = CardFactory.createCard(CardSuit.BLUE, 5);


        assertThrows(IllegalArgumentException.class,
                () -> roundService.playCard(player, invalidCard, false));
    }

    @Test
    void playCard_firstCardOfTrick_setsLeadingSuit() {
        prepareGameAndStartRound(3, "p1");
        Player player = players.get(0);
        ICard cardToPlay = CardFactory.createCard(CardSuit.RED, 5);
        player.getHandCards().clear();
        player.getHandCards().add(cardToPlay);

        roundService.playCard(player, cardToPlay, false);

        assertEquals(1, roundService.playedCards.size());
        assertEquals(cardToPlay, roundService.playedCards.get(0).second);
        assertEquals(0, player.getHandCards().size());
    }

    @Test
    void playCard_playerHasLeadingSuit_mustFollowSuit() {
        prepareGameAndStartRound(3, "p1");
        roundService.trumpCardSuit = CardSuit.YELLOW;
        Player player1 = players.get(0);
        Player player2 = players.get(1);

        ICard leadingCard = CardFactory.createCard(CardSuit.RED, 5);
        roundService.playedCards.add(new Pair<>(player1, leadingCard));

        ICard player2RedCard = CardFactory.createCard(CardSuit.RED, 8);
        ICard player2BlueCard = CardFactory.createCard(CardSuit.BLUE, 2);
        player2.getHandCards().clear();
        player2.getHandCards().addAll(List.of(player2RedCard, player2BlueCard));

        assertDoesNotThrow(() -> roundService.playCard(player2, player2RedCard, false));
        assertEquals(2, roundService.playedCards.size());
        assertEquals(1, player2.getHandCards().size());
    }

    @Test
    void playCard_playerHasLeadingSuit_cannotPlayOtherSuit() {
        prepareGameAndStartRound(3, "p1");
        roundService.trumpCardSuit = CardSuit.YELLOW;
        Player player1 = players.get(0);
        Player player2 = players.get(1);

        ICard leadingCard = CardFactory.createCard(CardSuit.RED, 5);
        roundService.playedCards.add(new Pair<>(player1, leadingCard));

        ICard player2RedCard = CardFactory.createCard(CardSuit.RED, 8);
        ICard player2BlueCard = CardFactory.createCard(CardSuit.BLUE, 2);
        player2.getHandCards().clear();
        player2.getHandCards().addAll(List.of(player2RedCard, player2BlueCard));

        assertThrows(IllegalStateException.class,
                () -> roundService.playCard(player2, player2BlueCard, false),
                "Sollte fehlschlagen, da Spieler die führende Farbe hat und diese bedienen muss.");
    }

    @Test
    void playCard_playerHasNoLeadingSuit_canPlayAnyCard() {
        prepareGameAndStartRound(3, "p1");
        roundService.trumpCardSuit = CardSuit.YELLOW;
        Player player1 = players.get(0);
        Player player2 = players.get(1);

        ICard leadingCard = CardFactory.createCard(CardSuit.RED, 5);
        roundService.playedCards.add(new Pair<>(player1, leadingCard));

        ICard player2BlueCard = CardFactory.createCard(CardSuit.BLUE, 2);
        player2.getHandCards().clear();
        player2.getHandCards().add(player2BlueCard);

        assertDoesNotThrow(() -> roundService.playCard(player2, player2BlueCard, false));
        assertEquals(2, roundService.playedCards.size());
        assertEquals(0, player2.getHandCards().size());
    }

    @Test
    void playCard_jesterCard_canAlwaysBePlayed() {
        prepareGameAndStartRound(3, "p1");
        roundService.trumpCardSuit = CardSuit.YELLOW;
        Player player1 = players.get(0);
        Player player2 = players.get(1);

        ICard leadingCard = CardFactory.createCard(CardSuit.RED, 5);
        roundService.playedCards.add(new Pair<>(player1, leadingCard));

        ICard jester = CardFactory.createCard(CardSuit.SPECIAL, 0); // Jester
        ICard player2RedCard = CardFactory.createCard(CardSuit.RED, 8);
        player2.getHandCards().clear();
        player2.getHandCards().addAll(List.of(player2RedCard, jester)); // Spieler hat Rot und Jester

        assertDoesNotThrow(() -> roundService.playCard(player2, jester, false));
        assertEquals(2, roundService.playedCards.size());
        assertTrue(player2.getHandCards().contains(player2RedCard)); // Rote Karte sollte noch da sein
    }

    @Test
    void playCard_wizardCard_canAlwaysBePlayed() {
        prepareGameAndStartRound(3, "p1");
        roundService.trumpCardSuit = CardSuit.YELLOW;
        Player player1 = players.get(0);
        Player player2 = players.get(1);

        ICard leadingCard = CardFactory.createCard(CardSuit.RED, 5);
        roundService.playedCards.add(new Pair<>(player1, leadingCard));

        ICard wizard = CardFactory.createCard(CardSuit.SPECIAL, 14); // Wizard
        ICard player2RedCard = CardFactory.createCard(CardSuit.RED, 8);
        player2.getHandCards().clear();
        player2.getHandCards().addAll(List.of(player2RedCard, wizard)); // Spieler hat Rot und Wizard


        assertDoesNotThrow(() -> roundService.playCard(player2, wizard, false));
        assertEquals(2, roundService.playedCards.size());
        assertTrue(player2.getHandCards().contains(player2RedCard)); // Rote Karte sollte noch da sein
    }

    @Test
    void playCard_playerHasNoLeadingSuitButHasTrump_mustPlayTrump() {
        prepareGameAndStartRound(3, "p1");
        roundService.trumpCardSuit = CardSuit.YELLOW;

        Player player1 = players.get(0);
        Player player2 = players.get(1);


        ICard leadingRedCard = CardFactory.createCard(CardSuit.RED, 5);
        roundService.playedCards.add(new Pair<>(player1, leadingRedCard));

        // Spieler hat GELBE Trumpfkarte, aber KEINE ROTE
        ICard player2YellowTrumpCard = CardFactory.createCard(CardSuit.YELLOW, 7); // Trumpfkarte
        ICard player2BlueCard = CardFactory.createCard(CardSuit.BLUE, 2); // Nicht-Trumpf, Nicht-Anspiel
        player2.getHandCards().clear();
        player2.getHandCards().addAll(List.of(player2YellowTrumpCard, player2BlueCard));

        assertDoesNotThrow(() -> roundService.playCard(player2, player2YellowTrumpCard, false),
                "Sollte erlaubt sein, da Spieler Anspielfarbe nicht hat und Trumpf legen MUSS.");

        assertEquals(2, roundService.playedCards.size());
        assertFalse(player2.getHandCards().contains(player2YellowTrumpCard));
    }

    @Test
    void playCard_playerHasNoLeadingSuitButHasTrump_cannotPlayNonTrump() {
        prepareGameAndStartRound(3, "p1");
        roundService.trumpCardSuit = CardSuit.YELLOW;
        Player player1 = players.get(0);
        Player player2 = players.get(1);

        ICard leadingRedCard = CardFactory.createCard(CardSuit.RED, 5);
        roundService.playedCards.add(new Pair<>(player1, leadingRedCard));

        ICard player2YellowTrumpCard = CardFactory.createCard(CardSuit.YELLOW, 7); // Trumpfkarte
        ICard player2BlueCard = CardFactory.createCard(CardSuit.BLUE, 2); // Nicht-Trumpf, Nicht-Anspiel
        player2.getHandCards().clear();
        player2.getHandCards().addAll(List.of(player2YellowTrumpCard, player2BlueCard));


        assertThrows(IllegalStateException.class,
                () -> roundService.playCard(player2, player2BlueCard, false),
                "Sollte fehlschlagen, da Spieler Anspielfarbe nicht hat, aber Trumpf legen MUSS.");

        assertEquals(1, roundService.playedCards.size());
        assertTrue(player2.getHandCards().contains(player2BlueCard));
    }


    @Test
    void endTrick_noCardsPlayed_throwsException() {
        prepareGameAndStartRound(3, "p1");
        assertThrows(IllegalStateException.class, () -> roundService.endTrick());
    }

    @Test
    void endTrick_determinesWinnerCorrectly_noTrump() {

        roundService.trumpCard = null;

        Player p1 = players.get(0);
        Player p2 = players.get(1);
        Player p3 = players.get(2);

        ICard red5 = CardFactory.createCard(CardSuit.RED, 5);
        ICard red10 = CardFactory.createCard(CardSuit.RED, 10);
        ICard blue8 = CardFactory.createCard(CardSuit.BLUE, 8);

        roundService.playedCards.add(new Pair<>(p1, red5));
        roundService.playedCards.add(new Pair<>(p2, red10)); // P2 sollte gewinnen
        roundService.playedCards.add(new Pair<>(p3, blue8)); // Falsche Farbe

        Player winner = roundService.endTrick();

        assertEquals(p2, winner);
        assertEquals(1, p2.getTricksWon());
        assertEquals(0, roundService.playedCards.size()); // Karten geleert
        assertEquals(1, roundService.currentTrickNumber); // Stichzähler erhöht
    }

    @Test
    void endTrick_determinesWinnerCorrectly_withTrump() {
        roundService.trumpCard = CardFactory.createCard(CardSuit.YELLOW, 0);
        roundService.trumpCardSuit = CardSuit.YELLOW; // Trumpffarbe ist Gelb

        Player p1 = players.get(0);
        Player p2 = players.get(1);
        Player p3 = players.get(2);

        ICard red5 = CardFactory.createCard(CardSuit.RED, 5);
        ICard yellow2 = CardFactory.createCard(CardSuit.YELLOW, 2); // Trumpf
        ICard red10 = CardFactory.createCard(CardSuit.RED, 10);

        roundService.playedCards.add(new Pair<>(p1, red5));
        roundService.playedCards.add(new Pair<>(p2, yellow2)); // P2 spielt Trumpf
        roundService.playedCards.add(new Pair<>(p3, red10));

        Player winner = roundService.endTrick();

        assertEquals(p2, winner); // P2 gewinnt mit Trumpf
        assertEquals(1, p2.getTricksWon());
    }

    @Test
    void endTrick_determinesWinnerCorrectly_withWizard() {
        roundService.trumpCard = CardFactory.createCard(CardSuit.RED, 0);
        roundService.trumpCardSuit = CardSuit.RED;

        Player p1 = players.get(0);
        Player p2 = players.get(1);
        Player p3 = players.get(2);

        ICard red5 = CardFactory.createCard(CardSuit.RED, 5);
        ICard wizard = CardFactory.createCard(CardSuit.SPECIAL, 14); // Wizard
        ICard red10 = CardFactory.createCard(CardSuit.RED, 10);

        roundService.playedCards.add(new Pair<>(p1, red5));
        roundService.playedCards.add(new Pair<>(p2, wizard)); // P2 spielt Wizard
        roundService.playedCards.add(new Pair<>(p3, red10));

        Player winner = roundService.endTrick();

        assertEquals(p2, winner); // Wizard gewinnt immer
        assertEquals(1, p2.getTricksWon());
    }

    @Test
    void endTrick_determinesWinnerCorrectly_withJester() {
        roundService.trumpCard = CardFactory.createCard(CardSuit.RED, 0);
        roundService.trumpCardSuit = CardSuit.RED;

        Player p1 = players.get(0);
        Player p2 = players.get(1);
        Player p3 = players.get(2);

        ICard red5 = CardFactory.createCard(CardSuit.RED, 5);
        ICard jester = CardFactory.createCard(CardSuit.SPECIAL, 0); // Jester
        ICard red10 = CardFactory.createCard(CardSuit.RED, 10);

        roundService.playedCards.add(new Pair<>(p1, red5));
        roundService.playedCards.add(new Pair<>(p2, jester));
        roundService.playedCards.add(new Pair<>(p3, red10));

        Player winner = roundService.endTrick();

        // Jester verliert immer
        assertEquals(p3, winner);
        assertEquals(1, p3.getTricksWon());
    }

    @Test
    void endRound_calculatesScoresCorrectly() {
        prepareGameAndStartRound(3, "p1");
        players.get(0).setPrediction(2); players.get(0).setTricksWon(2); // Perfect
        players.get(1).setPrediction(1); players.get(1).setTricksWon(3); // Under by 2
        players.get(2).setPrediction(3); players.get(2).setTricksWon(1); // Over by 2

        roundService.endRound();

        assertEquals(40, players.get(0).getScore());  // 20 + 2*10
        assertEquals(-20, players.get(1).getScore());  // -10 * 2
        assertEquals(-20, players.get(2).getScore()); // -10*2

        assertEquals(List.of(40), players.get(0).getRoundScores(), "Spieler 1: roundScores");
        assertEquals(List.of(-20), players.get(1).getRoundScores(), "Spieler 2: roundScores");
        assertEquals(List.of(-20), players.get(2).getRoundScores(), "Spieler 3: roundScores");

    }

    @Test
    void endRound_setsPredictionOrderStartingWithWinner() {

        players.get(0).setPrediction(1); players.get(0).setTricksWon(1);
        players.get(1).setPrediction(2); players.get(1).setTricksWon(2);
        players.get(2).setPrediction(3); players.get(2).setTricksWon(4);

        roundService.endRound();

        List<String> predictionOrder = game.getPredictionOrder();
        assertNotNull(predictionOrder);
        assertEquals(3, predictionOrder.size());
        assertEquals("p3", predictionOrder.get(0)); // Charlie = Gewinner
        assertTrue(predictionOrder.containsAll(List.of("p1", "p2", "p3")));
    }

    @Test
    void startRound_resetsPredictions() {
        players.forEach(p -> p.setPrediction(2));

        prepareGameAndStartRound(3, "p1");

        players.forEach(p -> assertNull(p.getPrediction(), "Prediction sollte zurückgesetzt sein"));
    }

    @Test
    void endRound_setsStatusToRoundEndSummaryAndSendsScoreboard() {
        prepareGameAndStartRound(3, "p1");
        players.get(0).setPrediction(1); players.get(0).setTricksWon(1);
        players.get(1).setPrediction(0); players.get(1).setTricksWon(0);
        players.get(2).setPrediction(0); players.get(2).setTricksWon(0);

        roundService.endRound();

        assertEquals(GameStatus.ROUND_END_SUMMARY, game.getStatus(),
                "Spielstatus sollte ROUND_END_SUMMARY sein.");

        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/game/" + game.getGameId() + "/scoreboard"),
                any(List.class)
        );

        verify(gameService, never()).processEndOfRound(anyString());
    }

    @Test
    void getPlayedCards_ShouldReturnEmptyListInitially() {
        RoundServiceImpl roundServiceImpl = new RoundServiceImpl(game, messagingTemplate, gameService);
        List<Pair<Player, ICard>> playedCards = roundServiceImpl.getPlayedCards();

        assertNotNull(playedCards);
        assertTrue(playedCards.isEmpty());
    }


    @Test
    void getTrumpCard_ShouldReturnNullInitially() {
        RoundServiceImpl roundService = new RoundServiceImpl(game, messagingTemplate, gameService);
        assertNull(roundService.getTrumpCard());
    }

    @Test
    void getTrumpCard_ShouldReturnSetTrumpCard() {
        RoundServiceImpl roundService = new RoundServiceImpl(game, messagingTemplate, gameService);
        ICard trumpCard = new WizardCard(CardSuit.SPECIAL);

        roundService.trumpCard = trumpCard;
        ICard result = roundService.getTrumpCard();
        assertEquals(trumpCard, result);
    }

}