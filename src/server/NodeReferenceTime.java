package server;

import java.time.LocalTime;

public class NodeReferenceTime {
    private LocalTime nodeTime;
    private long rtt;

    public NodeReferenceTime(LocalTime nodeTime, long rtt) {
        this.nodeTime = nodeTime;
        this.rtt = rtt;
    }

    public LocalTime getNodeTime() {
        return nodeTime;
    }

    public long getRTT() {
        return rtt;
    }

    public void setNodeTime(LocalTime nodeTime) {
        this.nodeTime = nodeTime;
    }
}
