package Websocket.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StompMessageTest {

    @Test
    void testStompMessageFields() {
        String from = "Alice";
        String text = "Hello World";

        StompMessage message = new StompMessage(from, text);

        assertEquals(from, message.getFrom());
        assertEquals(text, message.getText());
    }
}
