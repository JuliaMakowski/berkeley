package server;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BerkeleyAlg {
    public Map<String, Long> runAlg(Map<String, NodeReferenceTime> nodeReferenceTimeMap, long timeSent, long processTime) {
        long mainServerTime = getTimeOnMs(nodeReferenceTimeMap.get("SERVER").getNodeTime());
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

        if (filteredValues.size() == 0) filteredValues.add(mainServerTime);
        long sumFiltered = filteredValues.stream().reduce(0L, Long::sum);
        long avgFiltered = Math.round(sumFiltered * 1.0 / filteredValues.size());
        return nodeReferenceTimeMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                    NodeReferenceTime referenceTime = e.getValue();
                    long difference = avgFiltered - getTimeOnMs(referenceTime.getNodeTime());
                    long halfOfRtt = (timeSent - referenceTime.getReceivedTime()) / 2;
                    return difference + halfOfRtt + processTime;
                }));
        //@TODO tem algo aqui, que mesmo com a diferença grande, não ta tendo diferença...
        /*
        java client.Client 4 host 2004 11:00:05.000 0 0
        java client.Client 2 host 2002 10:00:05.000 0 0
        java client.Client 1 host 2001 10:00:05.000 0 0
        java client.Client 3 host 2000 10:00:05.000 0 0
         */
    }

    private boolean is10SecondsApart(long average, Long time) {
        return (average + 5000) > time && time > (average - 5000);
    }
    //b) Offset = Avg_1 - P_timestamp + (RTT / 2) <- COmo eu descubro o RTT


    private long getTimeOnMs(LocalTime time) {
        return time.toSecondOfDay() * 1000L + time.getNano() / 1000;
    }
}
