package domain;

import java.net.InetAddress;
import java.net.SocketAddress;

public class Message {
    private final InetAddress from;
    private final int port;
    private final String payload;

    public Message(InetAddress from, int port, String payload) {
        this.from = from;
        this.port = port;
        this.payload = payload;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getFrom() {
        return from;
    }

    public String getPayload() {
        return payload;
    }
}
