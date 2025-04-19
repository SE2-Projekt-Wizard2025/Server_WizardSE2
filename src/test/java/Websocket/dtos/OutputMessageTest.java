package Websocket.dtos;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class OutputMessageTest {

    @Test
    void testGetFrom() {
        OutputMessage message = new OutputMessage("Alice", "Hello!", "2025-04-04T12:00:00");
        assertEquals("Alice", message.getFrom());
    }

    @Test
    void testGetText() {
        OutputMessage message = new OutputMessage("Alice", "Hello!", "2025-04-04T12:00:00");
        assertEquals("Hello!", message.getText());
    }

    @Test
    void testGetTime() {
        OutputMessage message = new OutputMessage("Alice", "Hello!", "2025-04-04T12:00:00");
        assertEquals("2025-04-04T12:00:00", message.getTime());
    }
}
