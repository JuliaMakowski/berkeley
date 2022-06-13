package client;

import domain.Clock;
import domain.Message;
import domain.MessageTypes;

import java.io.IOException;
import java.net.*;
import java.util.function.Consumer;

public class ClientNode implements Consumer<Message> {

    private DatagramSocket socket;
    private String id;
    private long networkDelay;
    private Clock clock;


    public ClientNode(DatagramSocket socket, String id, long networkDelay, Clock clock) {
        this.socket = socket;
        this.id = id;
        this.clock = clock;
        this.networkDelay = networkDelay;
    }


    public void connect() throws IOException{
        InetAddress multiCastAddress = InetAddress.getByName("230.0.0.1");
        String req = "client_node_up;"+id;
        byte [] ip = req.getBytes();
        DatagramPacket packet = new DatagramPacket(ip, ip.length, multiCastAddress,5000);
        socket.send(packet);
    }

    @Override
    public void accept(Message receivedMessage) {
        try {
            String[] message = receivedMessage.getPayload().split(";");
            if (MessageTypes.ASK_CLOCK.name().equals(message[0])) {
                String request = MessageTypes.SEND_CLOCK + ";" + id + ";" + clock.getTime().toString();
                byte[] msg = request.getBytes();
                System.out.println("Problema de rede.. vou sleep por " + networkDelay + " ms");
                Thread.sleep(networkDelay);
                System.out.println("enviando pacote de resposta ao servidor...");
                socket.send(new DatagramPacket(msg, msg.length, receivedMessage.getFrom(), receivedMessage.getPort()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
