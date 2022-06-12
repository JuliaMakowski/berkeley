package client;

import domain.Clock;
import domain.IncomingReceiverMessage;

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
        String clocKDelay = args[4];
        String networkDelay = args[5];

        DatagramSocket socket = new DatagramSocket(Integer.parseInt(port));
        InetAddress address = InetAddress.getByName(host);
        Clock clock = new Clock(Integer.parseInt(clocKDelay), LocalTime.parse(time));
        ClientNode clientNode = new ClientNode(socket, id, networkDelay, clock);
        IncomingReceiverMessage incomingReceiverMessage = new IncomingReceiverMessage(socket, new ClientHandlerMessages(socket,clock));
        clientNode.listener();
    }
}
