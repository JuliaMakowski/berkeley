package server;

import domain.Clock;
import domain.IncomingReceiverMessage;
import domain.MessageTypes;

import java.io.IOException;
import java.net.*;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class MainNode extends Thread{
    private DatagramSocket socket ;
    private int nodesNumber;
    private Clock clock;
    private BerkeleyHandlerMessages berkeley;


    public MainNode(Clock clock, DatagramSocket socket, int nodesNumber, BerkeleyHandlerMessages handlerMessages) {
        this.socket = socket;
        this.clock = clock;
        this.nodesNumber = nodesNumber;
        this.berkeley = handlerMessages;
    }

    @Override
    public void run() {
        while (true) {
            String msg = MessageTypes.ASK_CLOCK.name();
            try {
                InetAddress multiCastAddress = InetAddress.getByName("230.0.0.1");
                berkeley.setTimeSent(clock.timeOnMs());
                byte[] byteMsg = msg.getBytes();
                DatagramPacket packet = new DatagramPacket(byteMsg, byteMsg.length, multiCastAddress,5000);
                socket.send(packet);
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(3000);
        Clock serverClock = new Clock(0, LocalTime.now());
        serverClock.start();
        BerkeleyHandlerMessages handler = new BerkeleyHandlerMessages(socket, 2, serverClock, 2);
        MainNode node = new MainNode(serverClock, socket, 3, handler);
        IncomingReceiverMessage serverReceiver = new IncomingReceiverMessage(socket, handler);
        serverReceiver.start();
        node.start();
    }
}
