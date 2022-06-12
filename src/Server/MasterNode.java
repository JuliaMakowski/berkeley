package Server;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

public class MasterNode extends Thread {

    private HashMap<Integer, String> clientList;
    private int port;
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];


    public MasterNode(int port){
        clientList = new HashMap<>();
        this.port = port;
    }

    public void Server()  {
        try {
            socket = new DatagramSocket(port);
            this.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

//  @Todo Metodo para poolling dos clientes quando o clock avisar
//  @Todo Metodo para calcular a média dos tempos

    public void run() {
        running = true;

        while (running) {
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            // @Todo Receber o tempo do cliente, precisamos definir o padão de send para arrumarmos o receiver
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received
                    = new String(packet.getData(), 0, packet.getLength());

            if (received.equals("end")) {
                running = false;
                continue;
            }
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }


}
