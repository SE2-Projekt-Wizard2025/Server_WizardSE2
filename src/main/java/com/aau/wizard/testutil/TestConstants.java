package com.aau.wizard.testutil;

import com.aau.wizard.model.enums.GameStatus;

public class TestConstants {
    public static final String TEST_GAME_ID = "12345";
    public static final GameStatus TEST_GAME_STATUS = GameStatus.LOBBY;

    public static final String TEST_PLAYER_ID = "Player1";
    public static final String TEST_PLAYER_NAME = "TestPlayer";
    public static final int TEST_PLAYER_SCORE = 0;
    public static final boolean TEST_PLAYER_READY = false;

    public static final String TEST_CARD = "RED-1";
    public static final String TEST_ACTION = "JOIN";
    public static final String TEST_LAST_PLAYED_CARD = "RED-5";

    public static final String TEST_CARD_COLOR = "RED";
    public static final String TEST_CARD_TYPE = "NORMAL";
    public static final String TEST_CARD_VALUE = "ONE";

    public static final String VALID_GAME_REQUEST_JSON = """
        {
          "gameId": "12345",
          "playerId": "Player1",
          "playerName": "TestPlayer",
          "card": "RED-1",
          "action": "JOIN"
        }
        """;

    public static final String VALID_GAME_RESPONSE_JSON = """
                                                            {
                                                              "gameId": "12345",
                                                              "status": "LOBBY",
                                                              "currentPlayerId": "Player1",
                                                              "players": [
                                                                {
                                                                  "playerId": "Player1",
                                                                  "playerName": "TestPlayer",
                                                                  "score": 0,
                                                                  "ready": false
                                                                },
                                                                {
                                                                  "playerId": "Player2",
                                                                  "playerName": "TestPlayer2",
                                                                  "score": 0,
                                                                  "ready": false
                                                                }
                                                              ],
                                                              "handCards": [
                                                                {
                                                                  "color": "RED",
                                                                  "value": "ONE",
                                                                  "type": "NORMAL"
                                                                },
                                                                {
                                                                  "color": "BLUE",
                                                                  "value": "TWO",
                                                                  "type": "FOOL"
                                                                }
                                                              ],
                                                              "lastPlayedCard": "RED-5"
                                                            }
                                                            """;
}
