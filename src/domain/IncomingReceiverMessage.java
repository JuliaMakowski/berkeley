package domain;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.function.Consumer;

public class IncomingReceiverMessage extends Listener {

    private Consumer<Message> handler;
    private DatagramSocket socket;

    public IncomingReceiverMessage(DatagramSocket socket, Consumer<Message> handler) {
        this.handler = handler;
        this.socket = socket;
    }

    @Override
    protected void execute() {
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                System.out.println("Will wait");
                socket.receive(packet);
                String payload = removeNull(packet);
                System.out.println("received from unicast: " + payload + " from: " + packet.getSocketAddress());
                handler.accept(new Message(packet.getAddress(), packet.getPort(), payload));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
