package client;

import domain.Clock;
import domain.IncomingReceiverMessage;
import domain.MulticastGroupListener;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalTime;

public class Client {
    public static void main(String[] args) throws IOException {
        String id = args[0];
        String host = args[1];
        String port = args[2];
        String time = args[3];
        String clockDelay = args[4];
        String networkDelay = args[5];

        InetAddress address = InetAddress.getByName(host);
        DatagramSocket socket = new DatagramSocket(Integer.parseInt(port), address);
        Clock clock = new Clock(Integer.parseInt(clockDelay), LocalTime.parse(time));
        clock.start();
        ClientNode clientNode = new ClientNode(socket, id, Long.parseLong(networkDelay), clock);
        IncomingReceiverMessage incomingReceiverMessage = new IncomingReceiverMessage(socket, new ClientHandlerMessages(clock));
        incomingReceiverMessage.start();
        clientNode.connect();
        MulticastGroupListener multicastGroupListener = new MulticastGroupListener(clientNode);
        multicastGroupListener.start();
    }
}
