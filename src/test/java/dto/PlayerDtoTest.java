package dto;

import com.aau.wizard.dto.PlayerDto;
import com.aau.wizard.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerDtoTest {

    private static final String PLAYER_ID = "player-1";
    private static final String PLAYER_NAME = "TestPlayer";
    private static final int SCORE = 42;
    private static final boolean READY = true;

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
        PlayerDto dto = createDefaultPlayerDtoAllArgsConstructor();

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
        assertEquals(PLAYER_ID, dto.getPlayerId());
        assertEquals(PLAYER_NAME, dto.getPlayerName());
        assertEquals(SCORE, dto.getScore());
        assertEquals(READY, dto.isReady());
    }

    private Player createDefaultPlayer() {
        Player player = new Player(PLAYER_ID, PLAYER_NAME);
        player.setScore(SCORE);
        player.setReady(READY);
        return player;
    }

    private PlayerDto createDefaultPlayerDtoNoArgsConstructor() {
        PlayerDto dto = new PlayerDto();
        dto.setPlayerId(PLAYER_ID);
        dto.setPlayerName(PLAYER_NAME);
        dto.setScore(SCORE);
        dto.setReady(READY);
        return dto;
    }

    private PlayerDto createDefaultPlayerDtoAllArgsConstructor() {
        return new PlayerDto(PLAYER_ID, PLAYER_NAME, SCORE, READY);
    }
}
