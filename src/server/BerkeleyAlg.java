package server;

import domain.Clock;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BerkeleyAlg {
    public Map<String, Long> runAlg(Map<String, NodeReferenceTime> nodeReferenceTimeMap, long timeSent, long processTime) {
        long mainServerTime = Clock.timeToMs(nodeReferenceTimeMap.get("SERVER").getNodeTime());
        long summedTimes = nodeReferenceTimeMap.values()
                .stream()
                .map(NodeReferenceTime::getNodeTime)
                .map(Clock::timeToMs)
                .reduce(0L, Long::sum);

        long average = Math.round(summedTimes * 1.0 / nodeReferenceTimeMap.size());
        System.out.println(" FIRST Average time is: " + toFormattedHour(average));
        List<Long> filteredValues = nodeReferenceTimeMap.values().stream()
                .map(NodeReferenceTime::getNodeTime)
                .peek(it -> System.out.println("All hours: " + it))
                .map(Clock::timeToMs)
                .filter(time -> is10SecondsApart(average, time))
                .collect(Collectors.toList());

        if (filteredValues.size() == 0) filteredValues.add(mainServerTime);
        long sumFiltered = filteredValues.stream().reduce(0L, Long::sum);
        long avgFiltered = Math.round(sumFiltered * 1.0 / filteredValues.size());
        System.out.println(" SECOND Average time is: " + toFormattedHour(avgFiltered));
        System.out.println("\n\n");
        Map<String, Long> resultMap = nodeReferenceTimeMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                    NodeReferenceTime referenceTime = e.getValue();
                    LocalTime nodeTime = referenceTime.getNodeTime();
                    long nodeTimeMs = Clock.timeToMs(nodeTime, true);
                    long rtt = timeSent - referenceTime.getReceivedTime();
                    System.out.println("node: *" + e.getKey() +  "* NodeTime: (" + nodeTime  + ") AVG: (" + toFormattedHour(avgFiltered) + ")");
                    System.out.println("node: *" + e.getKey() +  "* NodeTime: (" + nodeTimeMs  + ") AVG: (" + avgFiltered + ") RTT: (" + rtt + ")");
                    long difference = avgFiltered - nodeTimeMs;
                    long halfOfRtt = rtt / 2;
                    long result = difference + halfOfRtt + processTime;
                    System.out.println("Change to: " + result + "  to be: from: " + nodeTime + " to: " + operate(nodeTime, result));
                    System.out.println("-------------------------------------------------------------------");
                    return result;
                }));

        System.out.println("\n\n");
        return resultMap;
        /*
        java server.MainNode host 10:00:00.000 10 0 0
        java client.Client 4 host 2004 11:00:05.000 0 0
        java client.Client 2 host 2002 10:00:05.000 0 0
        java client.Client 1 host 2001 10:00:05.000 0 0
        java client.Client 3 host 2000 10:00:05.000 0 0
         */
    }

    public static void main(String[] args) {
        BerkeleyAlg berkeleyAlg = new BerkeleyAlg();
        LocalTime time = LocalTime.of(9,52,26);
        time = time.plus(500, ChronoUnit.MILLIS);
        LocalTime serverTime = LocalTime.of(10,0,46);

        long timeMs = Clock.timeToMs(time);
        long serverTimeMs = Clock.timeToMs(serverTime);

        System.out.println("time: " + time  +  " ms: " + timeMs);
        System.out.println("ServerTime: " + serverTime  +  " ms: " + serverTimeMs);

    }
    private boolean is10SecondsApart(long average, Long time) {
        return (average + 5000) > time && time > (average - 5000);
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
