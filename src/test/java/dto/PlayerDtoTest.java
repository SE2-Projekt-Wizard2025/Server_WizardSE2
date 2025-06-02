package dto;

import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static testutil.TestDataFactory.*;
import static testutil.TestConstants.*;

class PlayerDtoTest {
    /**
     * Verifies that the no-args constructor and setters work as expected.
     */
    @Test
    void testNoArgsConstructorAndSetters() {
        PlayerDto dto = createDefaultPlayerDtoNoArgsConstructor();

        assertPlayerDtoFields(dto);
    }

    /**
     * Verifies that the all-args constructor initializes all fields correctly.
     */
    @Test
    void testAllArgsConstructor() {
        PlayerDto dto = createDefaultPlayerDto();

        assertPlayerDtoFields(dto);
    }

    /**
     * Verifies that PlayerDto.from(Player) correctly maps all fields from the domain model.
     */
    @Test
    void testFromPlayer() {
        Player player = createDefaultPlayer();

        PlayerDto dto = PlayerDto.from(player);

        assertPlayerDtoFields(dto);
    }

    private void assertPlayerDtoFields(PlayerDto dto) {
        assertEquals(TEST_PLAYER_ID, dto.getPlayerId());
        assertEquals(TEST_PLAYER_NAME, dto.getPlayerName());
        assertEquals(TEST_PLAYER_SCORE, dto.getScore());
        assertEquals(TEST_PLAYER_READY, dto.isReady());
        assertEquals(TEST_PLAYER_PREDICTION, dto.getPrediction());

    }

    private PlayerDto createDefaultPlayerDtoNoArgsConstructor() {
        PlayerDto dto = new PlayerDto();
        dto.setPlayerId(TEST_PLAYER_ID);
        dto.setPlayerName(TEST_PLAYER_NAME);
        dto.setScore(TEST_PLAYER_SCORE);
        dto.setReady(TEST_PLAYER_READY);
        dto.setPrediction(TEST_PLAYER_PREDICTION);
        return dto;
    }
}
