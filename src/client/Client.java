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
        String adelay = args[4];

        DatagramSocket socket = new DatagramSocket(Integer.parseInt(port));
        InetAddress address = InetAddress.getByName(host);
        Clock clock = new Clock(adelay, LocalTime.parse(time));
        ClientNode clientNode = new ClientNode(socket, address, port, id, adelay, clock);
        clientNode.Listener();

        IncomingReceiverMessage incomingReceiverMessage = new IncomingReceiverMessage(socket, payload ->{

        });

    }
}
