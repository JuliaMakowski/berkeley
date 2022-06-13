package server;

import domain.Clock;
import domain.Message;
import domain.MessageTypes;

import java.beans.JavaBean;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BerkeleyHandlerMessages implements Consumer<Message> {
    private Map<NodeReference, NodeReferenceTime> clockNodes;
    private DatagramSocket socket;
    private int processTime;
    private Clock clock;
    private int nodesNumber;
    private long timeSent;

    public BerkeleyHandlerMessages(DatagramSocket socket, int startNodesNumber, Clock clock, int processTime) {
        this.clockNodes = new HashMap<>();
        this.socket = socket;
        this.nodesNumber = startNodesNumber;
        this.clock = clock;
        this.timeSent = 0;
        this.processTime = processTime;
    }

    public void setTimeSent(long time) {
        this.timeSent = time;
    }

    public void increaseNodeNumber() {
        nodesNumber++;
    }

    @Override
    public void accept(Message message) {
        String payload = message.getPayload();
        String[] messageParts = payload.split(";");
        System.out.println("Received message from process: " + messageParts[1] + " : " + payload);
        if (MessageTypes.SEND_CLOCK.name().equals(messageParts[0])) {
            NodeReference reference = new NodeReference(message.getFrom(), message.getPort(), messageParts[1]);
            NodeReferenceTime referenceTime = new NodeReferenceTime(LocalTime.parse(messageParts[2]), clock.timeOnMs());
            clockNodes.put(reference, referenceTime);
        }
        if (clockNodes.size() >= nodesNumber) {
            cleanMapAndExecute();
        }
    }

    private void cleanMapAndExecute() {
        Map<NodeReference, NodeReferenceTime> nodes = new HashMap<>(this.clockNodes);
        long currentTime = clock.timeOnMs();
        this.clockNodes.clear();
        long summedTimes = nodes.values().stream()
                .map(NodeReferenceTime::getNodeTime)
                .map(this::getTimeOnMs)
                .reduce(currentTime, Long::sum);
        long average = Math.round(summedTimes * 1.0 / nodes.size());
        long avgFiltered = nodes.values().stream()
                .map(NodeReferenceTime::getNodeTime)
                .map(this::getTimeOnMs)
                .filter(time -> is10SecondsApart(average, time))
                .reduce(is10SecondsApart(average, currentTime) ? currentTime : 0, Long::sum);
        Map<NodeReference, Long> valuesToChange = nodes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                    NodeReferenceTime referenceTime = e.getValue();
                    long difference = avgFiltered - getTimeOnMs(referenceTime.getNodeTime());
                    long halfOfRtt = (timeSent - referenceTime.getReceivedTime()) / 2;
                    return difference + halfOfRtt + processTime;
                }));
        try {
            Thread.sleep(processTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        changeCurrent(currentTime, avgFiltered);
        valuesToChange.forEach(this::adjustClock);
    }

    private void adjustClock(NodeReference nodeReference, Long time) {
        try {
            String operation = time < 0 ? "minus" : "plus";
            String msg = MessageTypes.FIX_CLOCK.name() + ";" + operation + ";" + Math.abs(time);
            byte[] byteMessage = msg.getBytes();
            System.out.println("Sending " + nodeReference.getId() + " message: " + msg);
            DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, nodeReference.getAddress(), nodeReference.getPort());
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void changeCurrent(long currentTime, long avgFiltered) {
        long difference = avgFiltered - currentTime;
        if (difference < 0) {
            clock.decrease(Math.abs(difference));
        } else {
            clock.increase(Math.abs(difference));
        }
    }

    private boolean is10SecondsApart(long average, Long time) {
        return time < average + 10000 || time > average - 10000;
    }
    //b) Offset = Avg_1 - P_timestamp + (RTT / 2) <- COmo eu descubro o RTT


    private long getTimeOnMs(LocalTime time) {
        return time.toSecondOfDay() * 1000L + time.getNano() / 1000;
    }
}
