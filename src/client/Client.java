package client;

import domain.Clock;

import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException {
        String id = args[0];
        String host = args[1];
        String port = args[2];
        String time = args[3];
        String ptime = args[4];
        String adelay = args[5];


        ClientNode clientNode = new ClientNode(host,port);
        clientNode.Listener();
    }
}
