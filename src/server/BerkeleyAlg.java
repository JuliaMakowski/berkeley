package server;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BerkeleyAlg {
    public Map<String, Long> runAlg(Map<String, NodeReferenceTime> nodeReferenceTimeMap, long timeSent, long processTime) {
        long summedTimes = nodeReferenceTimeMap.values()
                .stream()
                .map(NodeReferenceTime::getNodeTime)
                .map(this::getTimeOnMs)
                .reduce(0L, Long::sum);

        long average = Math.round(summedTimes * 1.0 / nodeReferenceTimeMap.size());
        List<Long> filteredValues = nodeReferenceTimeMap.values().stream()
                .map(NodeReferenceTime::getNodeTime)
                .map(this::getTimeOnMs)
                .filter(time -> is10SecondsApart(average, time))
                .collect(Collectors.toList());

        long sumFiltered = filteredValues.stream().reduce(0L, Long::sum);
        long avgFiltered = Math.round(sumFiltered * 1.0 / filteredValues.size());
        return nodeReferenceTimeMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                    NodeReferenceTime referenceTime = e.getValue();
                    long difference = avgFiltered - getTimeOnMs(referenceTime.getNodeTime());
                    long halfOfRtt = (timeSent - referenceTime.getReceivedTime()) / 2;
                    return difference + halfOfRtt + processTime;
                }));
    }

    private boolean is10SecondsApart(long average, Long time) {
        return time < average + 5000 || time > average - 5000;
    }
    //b) Offset = Avg_1 - P_timestamp + (RTT / 2) <- COmo eu descubro o RTT


    private long getTimeOnMs(LocalTime time) {
        return time.toSecondOfDay() * 1000L + time.getNano() / 1000;
    }
}
