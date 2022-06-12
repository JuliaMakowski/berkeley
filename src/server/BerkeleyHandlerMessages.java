package server;

import domain.Clock;
import domain.Message;
import domain.MessageTypes;

import java.beans.JavaBean;
import java.net.DatagramSocket;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BerkeleyHandlerMessages implements Consumer<Message> {
    private Map<NodeReference, LocalTime> clockNodes;
    private DatagramSocket socket;
    private Clock clock;
    private int nodesNumber;
    private long timeSent;

    public BerkeleyHandlerMessages(DatagramSocket socket, int nodesNumber, Clock clock) {
        this.clockNodes = new HashMap<>();
        this.socket = socket;
        this.nodesNumber = nodesNumber;
        this.clock = clock;
        timeSent = 0;
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
        if (MessageTypes.SEND_CLOCK.name().equals(messageParts[0])) {
            NodeReference reference = new NodeReference(message.getFrom(), messageParts[1]);
            clockNodes.put(reference, LocalTime.parse(messageParts[2]));
        }
        if (clockNodes.size() == nodesNumber) {
            cleanMapAndExecute();
        }
    }

    private void cleanMapAndExecute() {
        Map<NodeReference, LocalTime> nodes = new HashMap<>(this.clockNodes);
        this.clockNodes.clear();
        long currentTime = clock.timeOnMs();
        long rtt = currentTime - timeSent;
        timeSent = 0;
        long summedTimes = nodes.values().stream()
                .map(this::getTimeOnMs)
                .reduce(currentTime, Long::sum);
        long average = Math.round(summedTimes * 1.0 / nodes.size());
        long avgFiltered = nodes.values().stream()
                .map(this::getTimeOnMs)
                .filter(time -> is10SecondsApart(average, time))
                .reduce(is10SecondsApart(average, currentTime) ? currentTime : 0, Long::sum);

    }

    private boolean is10SecondsApart(long average, Long time) {
        return time < average + 10000 || time > average - 10000;
    }
    //b) Offset = Avg_1 - P_timestamp + (RTT / 2) <- COmo eu descubro o RTT


    private long getTimeOnMs(LocalTime time) {
        return time.toSecondOfDay() * 1000L + time.getNano() / 1000;
    }

    public static void main(String[] args) {
        LocalTime now = LocalTime.now();
        long ms = System.currentTimeMillis();
        System.out.println(ms);
        System.out.println(now.toString());
        System.out.println(now.getHour());
        System.out.println(now.getMinute());
        System.out.println(now.getSecond());
        System.out.println(now.getNano());

        System.out.println(now.toSecondOfDay() * 1000 + now.getNano() / 1000);
    }
}
