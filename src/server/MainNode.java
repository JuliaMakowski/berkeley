package server;

import domain.IncomingReceiverMessage;
import domain.MessageTypes;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class MainNode extends Thread{
    private DatagramSocket socket ;
    private int nodesNumber;


    public MainNode(DatagramSocket socket, int nodesNumber) {
        this.socket = socket;
        this.nodesNumber = nodesNumber;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramSocket socket = new DatagramSocket(3000);
        MainNode node = new MainNode(socket, 3);
        //Como fazer para saber todos os nodos..
        IncomingReceiverMessage serverReceiver = new IncomingReceiverMessage(socket, payload -> {

        });
    }

    @Override
    public void run() {
        while (true) {
            String msg = MessageTypes.ASK_CLOCK.name();
            InetAddress multiCastAddress = null;
            try {
                multiCastAddress = InetAddress.getByName("230.0.0.1");
                DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, multiCastAddress, 5000);
                socket.send(packet);
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
