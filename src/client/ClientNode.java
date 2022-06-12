package client;

import domain.Clock;
import domain.MessageTypes;

import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;

public class ClientNode extends Thread{


    private DatagramSocket socket;
    private InetAddress address;
    private String port;
    private String id;
    private Clock clock;


    private byte[] buf;


    public ClientNode(String host, String port, String id, Clock clock) {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName(host);
            this.port = port;
            this.id = id;
            this.clock = clock;
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
    }

    // @Todo Definir o que sera enviado, sugestão - host, time, ptime, adelay - separados ou por virgula ou por -
    public String sendMessage(String msg) {
        buf = msg.getBytes();
        //@Todo Definir porta do server aqui hardcoded ou definir de outro jeito?
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
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
        return new String(
                packet.getData(), 0, packet.getLength());
    }

    public void Listener() throws IOException {
        DatagramSocket socket = new DatagramSocket(Integer.parseInt(port));
        MulticastSocket mSocket = new MulticastSocket(5000);
        InetAddress grupo = InetAddress.getByName("230.0.0.1");
        mSocket.joinGroup(grupo);
        while (true) {
            byte[] entrada = new byte[1024];
            DatagramPacket packet = new DatagramPacket(entrada, entrada.length);
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
            if (MessageTypes.valueOf(message[0])==MessageTypes.FIX_CLOCK){ //ADJUST_CLOCK;plus|minus;T-ms
                if (message[1].equals("plus")) clock.increase(Integer.parseInt(message[2]));
                if (message[1].equals("minus")) clock.decrease(Integer.parseInt(message[2]));

                request = MessageTypes.SEND_CLOCK + ";" +id + ";"+ clock.getTime().toString();
                byte[] msg = request.getBytes(); // mandar SEND_CLOCK;id;time
                socket.send(new DatagramPacket(msg, msg.length, packet.getSocketAddress()));
            }

        }
    }

    public void close() {
        socket.close();
    }




}
