package client;

import domain.Clock;
import domain.MessageTypes;

import java.io.IOException;
import java.net.*;

public class ClientNode extends Thread{

    private DatagramSocket socket;
    private String id;
    private String networkDelay;
    private Clock clock;

    private byte[] buf;


    public ClientNode(DatagramSocket socket, String id, String networkDelay, Clock clock) {
        this.socket = socket;
        this.id = id;
        this.clock = clock;
        this.networkDelay = networkDelay;
    }


    public void send() throws IOException{
        byte[] entrada = new byte[1024];
        InetAddress multiCastAddress = InetAddress.getByName("230.0.0.1");
        String req = "client_node_up;"+id;
        byte [] ip = req.getBytes();

        DatagramPacket packet = new DatagramPacket(ip, ip.length, multiCastAddress,5000);

        socket.send(packet);
    }

    public void listener() throws IOException {
        MulticastSocket mSocket = new MulticastSocket(5000);
        InetAddress grupo = InetAddress.getByName("230.0.0.1");
        mSocket.joinGroup(grupo);
        byte[] entrada = new byte[1024];
        DatagramPacket packet = new DatagramPacket(entrada, entrada.length);

        while (true) {
            System.out.println("Wainting");
            mSocket.receive(packet);
            System.out.println("received");
            String recebido = new String(packet.getData(), 0, packet.getLength()); // validar tipo de mensagem
            String [] message = recebido.split(";");
            System.out.println("received: " + recebido + " from: " + packet.getSocketAddress());
            String request = "";
            if (MessageTypes.valueOf(message[0])==MessageTypes.ASK_CLOCK) {
                request = MessageTypes.SEND_CLOCK + ";" +id + ";"+ clock.getTime().toString();
                byte[] msg = request.getBytes(); // mandar SEND_CLOCK;id;time
                socket.send(new DatagramPacket(msg, msg.length, packet.getSocketAddress()));
            }
        }
    }

}
