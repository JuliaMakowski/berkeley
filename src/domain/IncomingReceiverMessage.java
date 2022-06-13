package domain;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.function.Consumer;

public class IncomingReceiverMessage extends Thread {

    private Consumer<Message> handler;
    private DatagramSocket socket;

    public IncomingReceiverMessage(DatagramSocket socket, Consumer<Message> handler) {
        this.handler = handler;
        this.socket = socket;
    }

    public String removeNull(DatagramPacket packet){
        byte[] data = packet.getData();
        String s = new String(packet.getData(), packet.getOffset(), packet.getData().length);
        int i = 0;
        char[] c = s.toCharArray();
        while (i < data.length) {
            if (packet.getData()[i] != 0) {
                break;
            }
            i++;
        }
        byte[] result;
        if (i > 0 && i < data.length) {
            result = Arrays.copyOfRange(data, i, data.length);
        } else {
            result = data;
        }
        return result.toString();
    }

    @Override
    public void run() {
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                System.out.println("Will wait");
                socket.receive(packet);
                String payload = removeNull(packet);
                //String payload = new String(packet.getData(), packet.getOffset(), packet.getData().length);
                handler.accept(new Message(packet.getSocketAddress(), payload));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
