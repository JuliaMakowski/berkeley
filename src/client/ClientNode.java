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
    private String adelay;
    private Clock clock;

    private byte[] buf;


    public ClientNode(DatagramSocket socket,InetAddress address, String port, String id, String adelay, Clock clock) {
        this.socket = socket;
        this.address = address;
        this.port = port;
        this.id = id;
        this.clock = clock;
        this.adelay = adelay;
    }

    // @Todo Definir o que sera enviado, sugestão - host, time, ptime, adelay - separados ou por virgula ou por -
    public void sendMessage(String msg) {
        buf = msg.getBytes();
        //@Todo Definir porta do server aqui hardcoded ou definir de outro jeito?
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 3000);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Listener() throws IOException {
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
            }

        }
    }

    public void close() {
        socket.close();
    }




}
