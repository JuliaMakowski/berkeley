package server;

import java.net.InetAddress;
import java.net.SocketAddress;

public class NodeReference {
    private final InetAddress address;
    private final int port;
    private final String id;

    public NodeReference(InetAddress address, int port, String id) {
        this.address = address;
        this.port = port;
        this.id = id;
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }

    public int getPort() {
        return port;
    }
}
