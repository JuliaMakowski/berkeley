package server;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
                    long result = difference + halfOfRtt + processTime;
                    //@TODO ENCHE DE LOG ATÈ ACHA O ERRO.
                    return result;
                }));
        //@TODO tem algo aqui, que mesmo com a diferença grande, não ta tendo diferença...
        /*
        java server.MainNode host 10:00:00.000 10 0 0
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

    private String toFormattedHour(long timeInMs) {
        long seconds = timeInMs / 1000;
        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;
        return HH + ":" + MM + ":" + SS + "." + timeInMs;
    }

    private LocalTime operate(LocalTime toChange, long howMuch) {
        if (howMuch < 0) {
            return toChange.minus(Math.abs(howMuch), ChronoUnit.MILLIS);
        } else {
            return toChange.plus(Math.abs(howMuch), ChronoUnit.MILLIS);
        }
    }
}
