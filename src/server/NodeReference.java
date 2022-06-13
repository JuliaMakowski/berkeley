package server;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeReference reference = (NodeReference) o;
        return getPort() == reference.getPort() && getAddress().equals(reference.getAddress()) && getId().equals(reference.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddress(), getPort(), getId());
    }
}
