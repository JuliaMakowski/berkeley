package server;

import domain.Clock;
import domain.Message;
import domain.MessageTypes;

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

    public BerkeleyHandlerMessages(DatagramSocket socket, int nodesNumber, Clock clock) {
        this.clockNodes = new HashMap<>();
        this.socket = socket;
        this.nodesNumber = nodesNumber;
        this.clock = clock;
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
        long currentTime = clock.getTime().toNanoOfDay();
        long summedTimes = nodes.values().stream()
                .map(LocalTime::toNanoOfDay)
                .reduce(currentTime, Long::sum);


    }
    //b) Offset = Avg_1 - P_timestamp + (RTT / 2) <- COmo eu descubro o RTT

    public void increaseNodeNumber() {
        nodesNumber++;
    }
}
