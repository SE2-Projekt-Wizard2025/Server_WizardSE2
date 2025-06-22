package controller.unit;

import com.aau.wizard.GameExceptions;
import com.aau.wizard.controller.GameWebSocketController;
import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.request.PredictionRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.model.enums.GameStatus;
import com.aau.wizard.service.interfaces.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static testutil.TestDataFactory.*;
import static testutil.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class GameWebSocketControllerTest {

    @Mock
    private GameService gameService;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private GameWebSocketController gameWebSocketController;

    /**
     * Tests that joinGame() returns a valid GameResponse when the GameService returns one.
     */
    @Test
    void testJoinGame() {
        GameRequest request = createDefaultGameRequest();
        GameResponse expectedResponse = createDefaultGameResponse(createDefaultPlayerDto());

        when(gameService.joinGame(any(GameRequest.class))).thenReturn(expectedResponse);

        gameWebSocketController.joinGame(request);

        verify(gameService, times(1)).joinGame(any(GameRequest.class));
        verify(messagingTemplate, times(1)).convertAndSend("/topic/game", expectedResponse);

    }

    @Test
    void testJoinGame_ServiceThrowsException() {
        GameRequest request = createDefaultGameRequest();
        String errorMessage = "Testfehler beim Beitreten";
        doThrow(new RuntimeException(errorMessage)).when(gameService).joinGame(any(GameRequest.class));

        gameWebSocketController.joinGame(request);

        verify(gameService, times(1)).joinGame(any(GameRequest.class));
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/errors/" + request.getPlayerId()),
                eq("Fehler beim Beitritt zum Spiel: " + errorMessage)
        );
    }

    @Test
    void testStartGame_Success() {
        String gameId = TEST_GAME_ID;
        GameResponse dummyResponse = new GameResponse(gameId, GameStatus.PLAYING, null, null, null, null, null, 1, null);
        when(gameService.startGame(gameId)).thenReturn(dummyResponse);

        gameWebSocketController.startGame(gameId);

        verify(gameService, times(1)).startGame(gameId);

    }

    @Test
    void testStartGameCleansQuotedGameId() {
        String quotedGameId = "\"test-game-id\"";
        String cleanGameId = "test-game-id";
        GameResponse dummyResponse = new GameResponse(cleanGameId, GameStatus.PLAYING, null, null, null, null, null, 1, null);
        when(gameService.startGame(cleanGameId)).thenReturn(dummyResponse);

        gameWebSocketController.startGame(quotedGameId);
        verify(gameService).startGame(cleanGameId);
    }

    @Test
    void testStartGame_GameNotFoundException() {
        String gameId = "nonExistentGame";
        String errorMessage = "Spiel nicht gefunden";
        doThrow(new GameExceptions.GameNotFoundException(errorMessage)).when(gameService).startGame(gameId);

        gameWebSocketController.startGame(gameId);

        verify(gameService, times(1)).startGame(gameId);
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/errors"),
                contains(errorMessage)
        );
    }

    @Test
    void testStartGame_GameStartException() {
        String gameId = TEST_GAME_ID;
        String errorMessage = "Spiel konnte nicht gestartet werden";
        doThrow(new GameExceptions.GameStartException(errorMessage)).when(gameService).startGame(gameId);

        gameWebSocketController.startGame(gameId);

        verify(gameService, times(1)).startGame(gameId);
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/errors"),
                contains(errorMessage)
        );
    }

    @Test
    void testStartGame_UnexpectedException() {
        String gameId = TEST_GAME_ID;
        doThrow(new RuntimeException("Unerwarteter Fehler")).when(gameService).startGame(gameId);

        gameWebSocketController.startGame(gameId);

        verify(gameService, times(1)).startGame(gameId);
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/errors"),
                eq("Ein unerwarteter Fehler ist beim Starten des Spiels aufgetreten.")
        );
    }

    @Test
    void testHandlePrediction_Success() {
        PredictionRequest request = new PredictionRequest(TEST_GAME_ID, TEST_PLAYER_ID, 3);
        GameResponse expectedResponse = createDefaultGameResponse(createDefaultPlayerDto());
        when(gameService.makePrediction(request)).thenReturn(expectedResponse);

        gameWebSocketController.handlePrediction(request);

        verify(gameService, times(1)).makePrediction(request);
        verify(messagingTemplate, times(1)).convertAndSend("/topic/game/" + request.getPlayerId(), expectedResponse);
    }

    @Test
    void testHandlePrediction_GameNotFoundException() {
        PredictionRequest request = new PredictionRequest(TEST_GAME_ID, TEST_PLAYER_ID, 3);
        String errorMessage = "Spiel nicht gefunden";
        doThrow(new GameExceptions.GameNotFoundException(errorMessage)).when(gameService).makePrediction(request);

        gameWebSocketController.handlePrediction(request);

        verify(gameService, times(1)).makePrediction(request);
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/errors/" + request.getPlayerId()),
                eq(errorMessage)
        );
    }

    @Test
    void testHandlePrediction_InvalidTurnException() {
        PredictionRequest request = new PredictionRequest(TEST_GAME_ID, TEST_PLAYER_ID, 3);
        String errorMessage = "Du bist noch nicht an der Reihe, bitte warte.";
        doThrow(new GameExceptions.InvalidTurnException(errorMessage)).when(gameService).makePrediction(request);

        gameWebSocketController.handlePrediction(request);

        verify(gameService, times(1)).makePrediction(request);
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/errors/" + request.getPlayerId()),
                eq(errorMessage)
        );
    }

    @Test
    void testHandlePrediction_UnexpectedException() {
        PredictionRequest request = new PredictionRequest(TEST_GAME_ID, TEST_PLAYER_ID, 3);
        doThrow(new RuntimeException("Unerwarteter interner Fehler")).when(gameService).makePrediction(request);

        gameWebSocketController.handlePrediction(request);

        verify(gameService, times(1)).makePrediction(request);
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/errors/" + request.getPlayerId()),
                eq("Ein unerwarteter Fehler ist aufgetreten.")
        );
    }


    @Test
    void testPlayCard_Success() {
        GameRequest request = createDefaultGameRequest();
        request.setCard("R10");
        GameResponse expectedResponse = createDefaultGameResponse(createDefaultPlayerDto());
        when(gameService.playCard(request)).thenReturn(expectedResponse);

        GameResponse actualResponse = gameWebSocketController.playCard(request);

        verify(gameService, times(1)).playCard(request);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testPlayCard_GameNotActiveException() {
        GameRequest request = createDefaultGameRequest();
        request.setCard("R10");
        String errorMessage = "Das Spiel ist nicht aktiv.";
        doThrow(new GameExceptions.GameNotActiveException(errorMessage)).when(gameService).playCard(request);

        GameResponse actualResponse = gameWebSocketController.playCard(request);

        verify(gameService, times(1)).playCard(request);
        assertNull(actualResponse);
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/errors/" + request.getPlayerId()),
                eq(errorMessage)
        );
    }

    @Test
    void testPlayCard_CardNotInHandException() {
        GameRequest request = createDefaultGameRequest();
        request.setCard("R10");
        String errorMessage = "Die Karte ist nicht in deiner Hand.";
        doThrow(new GameExceptions.CardNotInHandException(errorMessage)).when(gameService).playCard(request);

        GameResponse actualResponse = gameWebSocketController.playCard(request);

        verify(gameService, times(1)).playCard(request);
        assertNull(actualResponse);
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/errors/" + request.getPlayerId()),
                eq(errorMessage)
        );
    }

    @Test
    void testPlayCard_UnexpectedException() {
        GameRequest request = createDefaultGameRequest();
        request.setCard("R10");
        doThrow(new RuntimeException("Unerwarteter Fehler beim Spielen")).when(gameService).playCard(request);

        GameResponse actualResponse = gameWebSocketController.playCard(request);

        verify(gameService, times(1)).playCard(request);
        assertNull(actualResponse);
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/errors/" + request.getPlayerId()),
                eq("Ein unerwarteter Fehler ist beim Spielen der Karte aufgetreten.")
        );
    }

    @Test
    void testSendScoreboard_Success() {
        String gameId = TEST_GAME_ID;
        List<PlayerDto> expectedScoreboard = Collections.singletonList(createDefaultPlayerDto());
        when(gameService.getScoreboard(gameId)).thenReturn(expectedScoreboard);

        List<PlayerDto> actualScoreboard = gameWebSocketController.sendScoreboard(gameId);

        verify(gameService, times(1)).getScoreboard(gameId);
        assertEquals(expectedScoreboard, actualScoreboard);
    }

    @Test
    void testSendScoreboard_GameNotFoundException() {
        String gameId = "nonExistentGame";
        String errorMessage = "Spiel nicht gefunden";
        doThrow(new GameExceptions.GameNotFoundException(errorMessage)).when(gameService).getScoreboard(gameId);

        List<PlayerDto> actualScoreboard = gameWebSocketController.sendScoreboard(gameId);

        verify(gameService, times(1)).getScoreboard(gameId);
        assertTrue(actualScoreboard.isEmpty());
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/errors/" + gameId),
                contains(errorMessage)
        );
    }

    @Test
    void testSendScoreboard_UnexpectedException() {
        String gameId = TEST_GAME_ID;
        doThrow(new RuntimeException("DB Fehler")).when(gameService).getScoreboard(gameId);

        List<PlayerDto> actualScoreboard = gameWebSocketController.sendScoreboard(gameId);

        verify(gameService, times(1)).getScoreboard(gameId);
        assertTrue(actualScoreboard.isEmpty());
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/errors/" + gameId),
                eq("Ein unerwarteter Fehler ist beim Abrufen des Scoreboards aufgetreten.")
        );
    }

    @Test
    void testProceedToNextRound_Success() {
        String gameId = TEST_GAME_ID;
        doNothing().when(gameService).proceedToNextRound(gameId);

        gameWebSocketController.proceedToNextRound(gameId);

        verify(gameService, times(1)).proceedToNextRound(gameId);
    }

    @Test
    void testProceedToNextRound_withQuotes_Success() {
        String quotedGameId = "\"test-game-id\"";
        String cleanGameId = "test-game-id";
        doNothing().when(gameService).proceedToNextRound(cleanGameId);

        gameWebSocketController.proceedToNextRound(quotedGameId);

        verify(gameService, times(1)).proceedToNextRound(cleanGameId);
    }

    @Test
    void testProceedToNextRound_GameNotFoundException() {
        String gameId = "nonExistentGame";
        String errorMessage = "Spiel nicht gefunden";
        doThrow(new GameExceptions.GameNotFoundException(errorMessage)).when(gameService).proceedToNextRound(gameId);

        gameWebSocketController.proceedToNextRound(gameId);

        verify(gameService, times(1)).proceedToNextRound(gameId);
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/errors/" + gameId),
                contains(errorMessage)
        );
    }

    @Test
    void testProceedToNextRound_UnexpectedException() {
        String gameId = TEST_GAME_ID;
        doThrow(new RuntimeException("Unerwarteter Fehler beim Rundenfortschritt")).when(gameService).proceedToNextRound(gameId);

        gameWebSocketController.proceedToNextRound(gameId);

        verify(gameService, times(1)).proceedToNextRound(gameId);
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/errors/" + gameId),
                eq("Ein unerwarteter Fehler ist beim Fortfahren zur nächsten Runde aufgetreten.")
        );
    }

    private void assertBasicJoinResponse(GameResponse response) { /* ... */ }
    private void verifyJoinCalledOnce() { /* ... */ }
}