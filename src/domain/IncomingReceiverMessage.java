package domain;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.function.Consumer;

public class IncomingReceiverMessage extends Thread {

    private Consumer<Message> handler;
    private DatagramSocket socket;

    public IncomingReceiverMessage(DatagramSocket socket, Consumer<Message> handler) {
        this.handler = handler;
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                System.out.println("Will wait");
                socket.receive(packet);
                String payload = new String(packet.getData(), 0, packet.getData().length); // TODO Ju, faz um crop dos elementos null <- ta vindo sujeira aqui...
                System.out.println("Received back from: " + packet.getSocketAddress() + " the ack " + new String(packet.getData(), 0, packet.getData().length));
                handler.accept(new Message(packet.getSocketAddress(), payload));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
