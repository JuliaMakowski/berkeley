package client;

import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;

public class ClientNode extends Thread{


    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;


    public ClientNode(String host) {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName(host);
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
    }

    // @Todo Definir o que sera enviado, sugestão - host, time, ptime, adelay - separados ou por virgula ou por -
    public String sendMessage(String msg) {
        buf = msg.getBytes();
        //@Todo Definir porta do server aqui hardcoded ou definir de outro jeito?
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, 4445);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String received = new String(
                packet.getData(), 0, packet.getLength());
        return received;
    }

    public void close() {
        socket.close();
    }

//    public static void main(String[] args) {
//        String id = args[0];
//        String host = args[1];
//        String port = args[2];
//        String time = args[3];
//        String ptime = args[4];
//        String adelay = args[5];
//    }


}
