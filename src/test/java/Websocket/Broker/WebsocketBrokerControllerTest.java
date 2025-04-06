package Websocket.Broker;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Websocket.dtos.StompMessage;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WebsocketBrokerControllerTest {

    @Test
    void testHandleHello() {
        WebsocketBrokerController controller = new WebsocketBrokerController();
        String message = "Test Message";

        String response = controller.handleHello(message);

        assertEquals("Echo vom Broker: Test Message", response);
    }

    @Test
    void testHandleJson() {
        WebsocketBrokerController controller = new WebsocketBrokerController();
        StompMessage msg = new StompMessage("User1", "Hello World");

        StompMessage response = controller.handleJson(msg);

        assertEquals("User1", response.getFrom());
        assertEquals("Hello World", response.getText());
    }
}
