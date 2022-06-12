package client;

import domain.Clock;
import domain.Message;
import domain.MessageTypes;
import server.NodeReference;

import java.net.DatagramSocket;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ClientHandlerMessages implements Consumer<Message> {
    private Clock clock;

    public ClientHandlerMessages(Clock clock) {
        this.clock = clock;
    }
    @Override
    public void accept(Message message) {
        String payload = message.getPayload();
        String[] messageParts = payload.split(";");
        if (MessageTypes.FIX_CLOCK.name().equals(messageParts[0])) {
            if (messageParts[1].equals("plus")) clock.increase(Integer.parseInt(messageParts[2]));
            if (messageParts[1].equals("minus")) clock.decrease(Integer.parseInt(messageParts[2]));

        }
    }
}
