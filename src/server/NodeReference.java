package server;

import java.net.SocketAddress;

public class NodeReference {
    private final SocketAddress address;
    private final String id;

    public NodeReference(SocketAddress address, String id) {
        this.address = address;
        this.id = id;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }
}
