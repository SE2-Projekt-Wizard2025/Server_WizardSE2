package Websocket.Broker;

import Websocket.dtos.StompMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketBrokerController {

    @MessageMapping("/hello")
    @SendTo("/topic/hello-response")
    public String handleHello(String message) {
        System.out.println("STOMP empfangen: " + message);
        return "Echo vom Broker: " + message;
    }

    @MessageMapping("/object")
    @SendTo("/topic/rcv-object")
    public StompMessage handleJson(StompMessage msg) {
        System.out.println("Objekt empfangen: " + msg);
        return msg;
    }
}
