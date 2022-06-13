package server;

import java.time.LocalTime;

public class NodeReferenceTime {
    private LocalTime nodeTime;
    private long receivedTime;

    public NodeReferenceTime(LocalTime nodeTime, long rtt) {
        this.nodeTime = nodeTime;
        this.receivedTime = rtt;
    }

    public LocalTime getNodeTime() {
        return nodeTime;
    }

    public long getReceivedTime() {
        return receivedTime;
    }
}
