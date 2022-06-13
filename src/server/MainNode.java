package server;

import domain.Clock;
import domain.IncomingReceiverMessage;
import domain.MessageTypes;
import domain.MulticastGroupListener;

import java.io.IOException;
import java.net.*;
import java.time.LocalTime;

public class MainNode extends Thread {
    private DatagramSocket socket;
    private Clock clock;
    private Integer cycleTime;
    private BerkeleyHandlerMessages berkeley;


    public MainNode(Clock clock, Integer cycleTime, DatagramSocket socket, BerkeleyHandlerMessages handlerMessages) {
        this.socket = socket;
        this.clock = clock;
        this.berkeley = handlerMessages;
        this.cycleTime = cycleTime;
    }

    @Override
    public void run() {
        int flag = 0;
        while (true) {
            System.out.println("Asking clock to nodes at server time: " + clock.getTime());
            String msg = MessageTypes.ASK_CLOCK.name();
            try {
                InetAddress multiCastAddress = InetAddress.getByName("230.0.0.1");
                berkeley.setTimeSent(clock.timeOnMs());
                byte[] byteMsg = msg.getBytes();
                DatagramPacket packet = new DatagramPacket(byteMsg, byteMsg.length, multiCastAddress, 5000);
                System.out.println("Will Send packet: " + new String(packet.getData()));
                socket.send(packet);
                System.out.println("Will sleep for: " + cycleTime + " seconds ");
                //if (flag < 2) {
                    Thread.sleep(cycleTime * 1000);
                    flag++;
//                } else {
//                    System.out.println("WILL DIE");
//                    Thread.sleep(cycleTime * 1000000000);
//                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String host = args[0];
        String port = args[1];
        String time = args[2];
        String cycleTime = args[3];
        String processTime = args[4];
        String clockDelay = args[5];

        InetAddress serverHost = InetAddress.getByName(host);
        int serverPort = Integer.parseInt(port);
        DatagramSocket socket = new DatagramSocket(serverPort, serverHost);
        Clock serverClock = new Clock(Integer.parseInt(clockDelay), LocalTime.parse(time));
        serverClock.start();
        BerkeleyHandlerMessages berkeley = new BerkeleyHandlerMessages(socket, 0, serverClock, Integer.parseInt(processTime), new BerkeleyAlg());
        MulticastGroupListener multicastGroupListener = new MulticastGroupListener(message -> {
            if (!(message.getFrom().equals(serverHost) && message.getPort() == serverPort)) {
                System.out.println("Will increase node number because node: " + message.getFrom() + ":" + message.getPort() + " has connected");
                berkeley.increaseNodeNumber();
            }
        });
        MainNode node = new MainNode(serverClock, Integer.parseInt(cycleTime), socket, berkeley);
        IncomingReceiverMessage serverReceiver = new IncomingReceiverMessage(socket, berkeley);
        serverReceiver.start();
        multicastGroupListener.start();
        node.start();

    }
}
