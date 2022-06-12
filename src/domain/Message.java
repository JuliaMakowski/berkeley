package domain;

import java.net.SocketAddress;

public class Message {
    private final SocketAddress from;
    private final String payload;

    public Message(SocketAddress from, String payload) {
        this.from = from;
        this.payload = payload;
    }

    public SocketAddress getFrom() {
        return from;
    }

    public String getPayload() {
        return payload;
    }
}
