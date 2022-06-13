package domain;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.function.Consumer;

public class MulticastGroupListener extends Listener {

    private Consumer<Message> executor;
    private MulticastSocket mSocket;
    private InetAddress groupAddress;

    public MulticastGroupListener(Consumer<Message> executor) throws IOException {
        this.executor = executor;
        this.mSocket = new MulticastSocket(5000);
        this.groupAddress = InetAddress.getByName("230.0.0.1");
        mSocket.joinGroup(groupAddress);
    }

    @Override
    protected void execute() {
        try {
            System.out.println("Start mSocket listener");
            while (true) {
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                mSocket.receive(packet);
                String payload = removeNull(packet);
                System.out.println("received from multicast: " + payload + " from: " + packet.getSocketAddress());
                executor.accept(new Message(packet.getAddress(), packet.getPort(), payload));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
