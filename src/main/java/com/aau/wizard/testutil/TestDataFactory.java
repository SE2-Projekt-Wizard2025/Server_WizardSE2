package com.aau.wizard.testutil;

import com.aau.wizard.dto.CardDto;
import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.dto.request.GameRequest;
import com.aau.wizard.dto.response.GameResponse;
import com.aau.wizard.model.Card;
import com.aau.wizard.model.Game;
import com.aau.wizard.model.Player;
import com.aau.wizard.model.enums.CardColor;
import com.aau.wizard.model.enums.CardType;
import com.aau.wizard.model.enums.CardValue;

import java.util.List;

import static com.aau.wizard.testutil.TestConstants.*;

/**
 * Utility class for creating test data.
 * Excluded from code coverage.
 */
public class TestDataFactory {

    public static Game createDefaultGame() {
        return new Game(TEST_GAME_ID);
    }

    public static PlayerDto createDefaultPlayerDto() {
        return new PlayerDto(TEST_PLAYER_ID, TEST_PLAYER_NAME, TEST_PLAYER_SCORE, TEST_PLAYER_READY);
    }

    public static PlayerDto createCustomPlayerDto(String playerId, String playerName, int score, boolean ready) {
        return new PlayerDto(playerId, playerName, score, ready);
    }

    public static Player createDefaultPlayer() {
        return new Player(TEST_PLAYER_ID, TEST_PLAYER_NAME);
    }

    public static Player createCustomPlayer(String playerId, String playerName) {
        return new Player(playerId, playerName);
    }

    public static CardDto createDefaultCardDto() {
        return new CardDto(TEST_CARD_COLOR, TEST_CARD_VALUE, TEST_CARD_TYPE);
    }

    public static CardDto createCustomCardDto(String cardColor, String cardValue, String cardType) {
        return new CardDto(cardColor, cardValue, cardType);
    }

    public static Card createDefaultCard() {
        return new Card(CardColor.RED, CardValue.ONE, CardType.NORMAL);
    }

    public static Card createCustomCard(CardColor cardColor, CardValue cardValue, CardType cardType) {
        return new Card(cardColor, cardValue, cardType);
    }

    public static List<PlayerDto> createDefaultListOfPlayerDto() {
        return List.of(
                createDefaultPlayerDto(),
                createCustomPlayerDto("Player2", "TestPlayer2", 0, false)
        );
    }

    public static List<Player> createDefaultListOfPlayer() {
        return List.of(
                createDefaultPlayer(),
                createCustomPlayer("Player2", "TestPlayer2")
        );
    }

    public static List<CardDto> createDefaultListOfCardDto() {
        return List.of(
                createDefaultCardDto(),
                createCustomCardDto("BLUE", "TWO", "FOOL")
        );
    }

    public static List<Card> createDefaultListOfCard() {
        return List.of(
                createDefaultCard(),
                createCustomCard(CardColor.BLUE, CardValue.TWO, CardType.FOOL)
        );
    }

    public static GameResponse createDefaultGameResponse(PlayerDto testPlayer) {
        return new GameResponse(
                TEST_GAME_ID,
                TEST_GAME_STATUS,
                TEST_PLAYER_ID,
                createDefaultListOfPlayerDto(),
                createDefaultListOfCardDto(),
                TEST_LAST_PLAYED_CARD
        );
    }

    public static GameRequest createDefaultGameRequest() {
        GameRequest request = new GameRequest();
        request.setGameId(TEST_GAME_ID);
        request.setPlayerId(TEST_PLAYER_ID);
        request.setPlayerName(TEST_PLAYER_NAME);
        request.setCard(TEST_CARD);
        request.setAction(TEST_ACTION);
        return request;
    }

    public static GameRequest createCustomGameRequest(String gameId, String playerId, String playerName) {
        GameRequest request = new GameRequest();
        request.setGameId(gameId);
        request.setPlayerId(playerId);
        request.setPlayerName(playerName);
        return request;
    }
}
